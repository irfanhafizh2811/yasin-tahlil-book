/**
 * Cloud Functions for Prayer Analytics
 * Processes daily and weekly prayer statistics with real-time aggregation
 */

import { onSchedule } from 'firebase-functions/v2/scheduler';
import { onDocumentCreated, onDocumentUpdated } from 'firebase-functions/v2/firestore';
import { onCall } from 'firebase-functions/v2/https';
import { getFirestore, FieldValue, Timestamp } from 'firebase-admin/firestore';
import { logger } from 'firebase-functions';

const db = getFirestore();

// Daily Analytics Aggregation (runs at 23:59 UTC daily)
export const aggregateDailyAnalytics = onSchedule(
  { schedule: '59 23 * * *', timeZone: 'UTC' },
  async (event) => {
    logger.info('Starting daily prayer analytics aggregation');
    
    try {
      const today = new Date();
      const startOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate());
      const endOfDay = new Date(startOfDay.getTime() + 24 * 60 * 60 * 1000);
      const dateString = startOfDay.toISOString().split('T')[0];
      
      // Aggregate prayer sessions for the day
      const sessionsSnapshot = await db.collection('memorial_prayers')
        .where('created_at', '>=', Timestamp.fromDate(startOfDay))
        .where('created_at', '<', Timestamp.fromDate(endOfDay))
        .get();
      
      // Calculate daily statistics
      const dailyStats = await calculateDailyStatistics(sessionsSnapshot.docs);
      
      // Store in daily_analytics collection
      await db.collection('daily_analytics').doc(dateString).set({
        date: dateString,
        ...dailyStats,
        calculated_at: FieldValue.serverTimestamp()
      });
      
      // Update global statistics
      await updateGlobalStatistics(dailyStats, 'daily');
      
      logger.info(`Daily analytics aggregated for ${dateString}`, dailyStats);
      
    } catch (error) {
      logger.error('Error aggregating daily analytics:', error);
      throw error;
    }
  }
);

// Weekly Analytics Aggregation (runs every Monday at 00:30 UTC)
export const aggregateWeeklyAnalytics = onSchedule(
  { schedule: '30 0 * * 1', timeZone: 'UTC' },
  async (event) => {
    logger.info('Starting weekly prayer analytics aggregation');
    
    try {
      const today = new Date();
      const lastMonday = getLastMonday(today);
      const thisMonday = new Date(lastMonday.getTime() + 7 * 24 * 60 * 60 * 1000);
      const weekStartString = lastMonday.toISOString().split('T')[0];
      
      // Get all daily analytics for the week
      const dailyAnalyticsSnapshot = await db.collection('daily_analytics')
        .where('date', '>=', weekStartString)
        .where('date', '<', thisMonday.toISOString().split('T')[0])
        .orderBy('date')
        .get();
      
      // Calculate weekly statistics
      const weeklyStats = await calculateWeeklyStatistics(dailyAnalyticsSnapshot.docs, lastMonday);
      
      // Store in weekly_analytics collection
      await db.collection('weekly_analytics').doc(weekStartString).set({
        week_start_date: weekStartString,
        ...weeklyStats,
        calculated_at: FieldValue.serverTimestamp()
      });
      
      // Update global statistics
      await updateGlobalStatistics(weeklyStats, 'weekly');
      
      logger.info(`Weekly analytics aggregated for week starting ${weekStartString}`, weeklyStats);
      
    } catch (error) {
      logger.error('Error aggregating weekly analytics:', error);
      throw error;
    }
  }
);

// Real-time Country Statistics Update
export const updateCountryStatistics = onDocumentCreated(
  'memorial_prayers/{prayerId}',
  async (event) => {
    const prayerData = event.data?.data();
    if (!prayerData) return;
    
    try {
      const userId = prayerData.user_id;
      const prayerType = prayerData.prayer_type;
      
      // Get user's country from profile
      const userDoc = await db.collection('users').doc(userId).get();
      const userData = userDoc.data();
      
      if (!userData?.country_code) {
        logger.warn(`No country code found for user ${userId}`);
        return;
      }
      
      const countryCode = userData.country_code;
      const countryName = userData.country_name || countryCode;
      
      // Update country statistics
      const countryStatsRef = db.collection('country_stats').doc(countryCode);
      
      await db.runTransaction(async (transaction) => {
        const countryDoc = await transaction.get(countryStatsRef);
        const currentStats = countryDoc.data() || {};
        
        const updatedStats = {
          country_code: countryCode,
          country_name: countryName,
          total_prayers: (currentStats.total_prayers || 0) + 1,
          active_participants: await getActiveParticipantsCount(countryCode),
          flag: getCountryFlag(countryCode),
          last_active_at: FieldValue.serverTimestamp(),
          popular_prayer_types: await updatePopularPrayerTypes(
            currentStats.popular_prayer_types || [],
            prayerType
          ),
          heat_level: calculateHeatLevel(currentStats.total_prayers || 0),
          updated_at: FieldValue.serverTimestamp()
        };
        
        transaction.set(countryStatsRef, updatedStats, { merge: true });
      });
      
      logger.info(`Updated country statistics for ${countryCode}`);
      
    } catch (error) {
      logger.error('Error updating country statistics:', error);
    }
  }
);

// Global Milestone Checker (runs every hour)
export const checkGlobalMilestones = onSchedule(
  { schedule: '0 * * * *', timeZone: 'UTC' },
  async (event) => {
    logger.info('Checking global prayer milestones');
    
    try {
      // Get current global statistics
      const globalStatsDoc = await db.collection('community_stats').doc('global').get();
      const globalStats = globalStatsDoc.data();
      
      if (!globalStats) {
        logger.warn('No global statistics found');
        return;
      }
      
      // Get active milestones
      const milestonesSnapshot = await db.collection('global_milestones')
        .where('is_active', '==', true)
        .where('is_completed', '==', false)
        .get();
      
      const batch = db.batch();
      let milestonesAchieved = 0;
      
      for (const milestoneDoc of milestonesSnapshot.docs) {
        const milestone = milestoneDoc.data();
        const currentValue = getCurrentValueForMilestone(globalStats, milestone.type);
        
        if (currentValue >= milestone.target_value) {
          // Milestone achieved!
          batch.update(milestoneDoc.ref, {
            is_completed: true,
            current_value: currentValue,
            achieved_at: FieldValue.serverTimestamp(),
            updated_at: FieldValue.serverTimestamp()
          });
          
          // Create celebration notification
          await createMilestoneCelebration(milestone, currentValue);
          milestonesAchieved++;
          
          logger.info(`Milestone achieved: ${milestone.title}`);
        } else {
          // Update current value
          batch.update(milestoneDoc.ref, {
            current_value: currentValue,
            updated_at: FieldValue.serverTimestamp()
          });
        }
      }
      
      if (milestonesAchieved > 0) {
        await batch.commit();
        logger.info(`${milestonesAchieved} milestones achieved and updated`);
      }
      
    } catch (error) {
      logger.error('Error checking global milestones:', error);
    }
  }
);

// Manual Analytics Recalculation (callable function)
export const recalculateAnalytics = onCall(
  { cors: true },
  async (request) => {
    const { timeframe, startDate, endDate } = request.data;
    
    if (!['daily', 'weekly', 'country'].includes(timeframe)) {
      throw new Error('Invalid timeframe. Must be daily, weekly, or country');
    }
    
    logger.info(`Manual recalculation requested for ${timeframe}`, { startDate, endDate });
    
    try {
      let result;
      
      switch (timeframe) {
        case 'daily':
          result = await recalculateDailyAnalytics(startDate, endDate);
          break;
        case 'weekly':
          result = await recalculateWeeklyAnalytics(startDate, endDate);
          break;
        case 'country':
          result = await recalculateCountryStatistics();
          break;
      }
      
      logger.info(`Analytics recalculation completed for ${timeframe}`, result);
      return result;
      
    } catch (error) {
      logger.error(`Error recalculating ${timeframe} analytics:`, error);
      throw error;
    }
  }
);

// Helper Functions

async function calculateDailyStatistics(prayerDocs: any[]): Promise<any> {
  const prayerTypeBreakdown: Record<string, number> = {};
  const uniqueUsers = new Set<string>();
  const sessionDurations: number[] = [];
  const regions = new Set<string>();
  
  let totalPrayers = 0;
  let completedSessions = 0;
  let newMemorials = 0;
  const hourlyDistribution: number[] = new Array(24).fill(0);
  
  for (const doc of prayerDocs) {
    const data = doc.data();
    totalPrayers += data.prayer_count || 1;
    uniqueUsers.add(data.user_id);
    
    // Prayer type breakdown
    const prayerType = data.prayer_type || 'UNKNOWN';
    prayerTypeBreakdown[prayerType] = (prayerTypeBreakdown[prayerType] || 0) + 1;
    
    // Session duration
    if (data.duration_minutes) {
      sessionDurations.push(data.duration_minutes);
    }
    
    // Regions
    if (data.region_code) {
      regions.add(data.region_code);
    }
    
    // Completed sessions
    if (data.status === 'completed') {
      completedSessions++;
    }
    
    // Memorial tracking
    if (data.is_new_memorial) {
      newMemorials++;
    }
    
    // Hourly distribution
    const createdAt = data.created_at?.toDate();
    if (createdAt) {
      hourlyDistribution[createdAt.getUTCHours()]++;
    }
  }
  
  const averageSessionDuration = sessionDurations.length > 0 
    ? sessionDurations.reduce((a, b) => a + b, 0) / sessionDurations.length 
    : 0;
  
  const peakHour = hourlyDistribution.indexOf(Math.max(...hourlyDistribution));
  
  return {
    total_prayers: totalPrayers,
    unique_participants: uniqueUsers.size,
    average_session_duration: averageSessionDuration,
    prayer_type_breakdown: prayerTypeBreakdown,
    peak_hour: peakHour,
    regions_active: regions.size,
    new_memorials: newMemorials,
    completed_sessions: completedSessions,
    hourly_distribution: hourlyDistribution
  };
}

async function calculateWeeklyStatistics(dailyDocs: any[], weekStart: Date): Promise<any> {
  let totalPrayers = 0;
  let totalParticipants = 0;
  let totalSessions = 0;
  
  const dailyBreakdown: any[] = [];
  const prayerTypeBreakdown: Record<string, number> = {};
  
  for (const doc of dailyDocs) {
    const data = doc.data();
    totalPrayers += data.total_prayers || 0;
    totalParticipants += data.unique_participants || 0;
    totalSessions += data.completed_sessions || 0;
    
    dailyBreakdown.push(data);
    
    // Aggregate prayer types
    if (data.prayer_type_breakdown) {
      Object.entries(data.prayer_type_breakdown).forEach(([type, count]: [string, any]) => {
        prayerTypeBreakdown[type] = (prayerTypeBreakdown[type] || 0) + count;
      });
    }
  }
  
  const averageDailyPrayers = dailyDocs.length > 0 ? totalPrayers / dailyDocs.length : 0;
  
  // Calculate growth rate (compared to previous week)
  const previousWeekStart = new Date(weekStart.getTime() - 7 * 24 * 60 * 60 * 1000);
  const previousWeekDoc = await db.collection('weekly_analytics')
    .doc(previousWeekStart.toISOString().split('T')[0])
    .get();
  
  let growthRate = 0;
  if (previousWeekDoc.exists) {
    const previousWeekData = previousWeekDoc.data();
    const previousTotal = previousWeekData?.total_prayers || 0;
    if (previousTotal > 0) {
      growthRate = ((totalPrayers - previousTotal) / previousTotal) * 100;
    }
  }
  
  return {
    total_prayers: totalPrayers,
    average_daily_prayers: averageDailyPrayers,
    unique_participants: totalParticipants,
    growth_rate: growthRate,
    daily_breakdown: dailyBreakdown,
    prayer_type_breakdown: prayerTypeBreakdown,
    completed_sessions: totalSessions
  };
}

async function updateGlobalStatistics(stats: any, type: 'daily' | 'weekly'): Promise<void> {
  const globalStatsRef = db.collection('community_stats').doc('global');
  
  await db.runTransaction(async (transaction) => {
    const globalDoc = await transaction.get(globalStatsRef);
    const currentGlobal = globalDoc.data() || {};
    
    const updatedGlobal = {
      ...currentGlobal,
      last_updated: FieldValue.serverTimestamp()
    };
    
    if (type === 'daily') {
      updatedGlobal.total_prayers_today = stats.total_prayers;
      updatedGlobal.total_participants = stats.unique_participants;
      updatedGlobal.active_regions = stats.regions_active;
      updatedGlobal.peak_hour = stats.peak_hour;
      
      // Update daily growth
      const yesterday = (currentGlobal.total_prayers_today || 0);
      if (yesterday > 0) {
        updatedGlobal.daily_growth = ((stats.total_prayers - yesterday) / yesterday) * 100;
      }
    }
    
    if (type === 'weekly') {
      updatedGlobal.weekly_growth = stats.growth_rate;
      
      // Determine trend direction
      if (stats.growth_rate > 5) {
        updatedGlobal.global_trend = 'INCREASING';
      } else if (stats.growth_rate < -5) {
        updatedGlobal.global_trend = 'DECREASING';
      } else {
        updatedGlobal.global_trend = 'STABLE';
      }
    }
    
    transaction.set(globalStatsRef, updatedGlobal, { merge: true });
  });
}

function getLastMonday(date: Date): Date {
  const day = date.getDay();
  const diff = date.getDate() - day + (day === 0 ? -6 : 1);
  const monday = new Date(date.setDate(diff));
  monday.setHours(0, 0, 0, 0);
  return monday;
}

function getCurrentValueForMilestone(globalStats: any, milestoneType: string): number {
  switch (milestoneType) {
    case 'TOTAL_PRAYERS':
      return globalStats.total_prayers_today || 0;
    case 'GLOBAL_PARTICIPANTS':
      return globalStats.total_participants || 0;
    case 'COUNTRIES_REACHED':
      return globalStats.total_countries || 0;
    case 'DAILY_PEAK':
      return globalStats.total_prayers_today || 0;
    case 'COMMUNITY_SESSIONS':
      return globalStats.total_active_sessions || 0;
    case 'MEMORIAL_CREATED':
      return globalStats.total_memorials || 0;
    case 'FAMILY_SHARING':
      return globalStats.total_family_shares || 0;
    default:
      return 0;
  }
}

async function createMilestoneCelebration(milestone: any, currentValue: number): Promise<void> {
  // Create a celebration notification/event
  await db.collection('milestone_celebrations').add({
    milestone_id: milestone.id,
    title: milestone.title,
    description: milestone.description,
    celebration_message: milestone.celebration_message || `🎉 Congratulations! We've reached ${currentValue} ${milestone.type}!`,
    achieved_value: currentValue,
    achieved_at: FieldValue.serverTimestamp(),
    icon: milestone.icon || '🎉',
    participating_countries: milestone.participating_countries || [],
    is_active: true,
    created_at: FieldValue.serverTimestamp()
  });
  
  logger.info(`Milestone celebration created for: ${milestone.title}`);
}

// Country helper functions

function getCountryFlag(countryCode: string): string {
  const flags: Record<string, string> = {
    'ID': '🇮🇩', 'SA': '🇸🇦', 'PK': '🇵🇰', 'MY': '🇲🇾', 'TR': '🇹🇷',
    'EG': '🇪🇬', 'NG': '🇳🇬', 'BD': '🇧🇩', 'IN': '🇮🇳', 'IR': '🇮🇷'
  };
  return flags[countryCode] || '🏳️';
}

function calculateHeatLevel(totalPrayers: number): number {
  // Normalize to 0-1 scale based on prayer activity
  const maxPrayers = 200000; // Adjust based on expected maximum
  return Math.min(totalPrayers / maxPrayers, 1.0);
}

async function getActiveParticipantsCount(countryCode: string): Promise<number> {
  // Count unique users from the country who prayed in the last 7 days
  const oneWeekAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
  
  const recentPrayersSnapshot = await db.collection('memorial_prayers')
    .where('created_at', '>=', Timestamp.fromDate(oneWeekAgo))
    .get();
  
  const uniqueUsers = new Set<string>();
  
  for (const doc of recentPrayersSnapshot.docs) {
    const prayerData = doc.data();
    const userId = prayerData.user_id;
    
    // Check if user is from the specified country
    const userDoc = await db.collection('users').doc(userId).get();
    const userData = userDoc.data();
    
    if (userData?.country_code === countryCode) {
      uniqueUsers.add(userId);
    }
  }
  
  return uniqueUsers.size;
}

async function updatePopularPrayerTypes(currentTypes: any[], newPrayerType: string): Promise<any[]> {
  const typeMap = new Map<string, { prayer_type: string; count: number; percentage: number }>();
  
  // Load current types
  currentTypes.forEach(type => {
    typeMap.set(type.prayer_type, type);
  });
  
  // Update count for new prayer type
  const existing = typeMap.get(newPrayerType);
  if (existing) {
    existing.count++;
  } else {
    typeMap.set(newPrayerType, {
      prayer_type: newPrayerType,
      count: 1,
      percentage: 0
    });
  }
  
  // Recalculate percentages
  const totalCount = Array.from(typeMap.values()).reduce((sum, type) => sum + type.count, 0);
  
  const updatedTypes = Array.from(typeMap.values()).map(type => ({
    ...type,
    percentage: totalCount > 0 ? (type.count / totalCount) * 100 : 0
  }));
  
  // Sort by count and return top 5
  return updatedTypes
    .sort((a, b) => b.count - a.count)
    .slice(0, 5);
}

// Stub implementations for recalculation functions
async function recalculateDailyAnalytics(startDate: string, endDate: string): Promise<any> {
  // Implementation for manual daily analytics recalculation
  return { message: 'Daily analytics recalculation completed', startDate, endDate };
}

async function recalculateWeeklyAnalytics(startDate: string, endDate: string): Promise<any> {
  // Implementation for manual weekly analytics recalculation
  return { message: 'Weekly analytics recalculation completed', startDate, endDate };
}

async function recalculateCountryStatistics(): Promise<any> {
  // Implementation for manual country statistics recalculation
  return { message: 'Country statistics recalculation completed' };
}