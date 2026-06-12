package com.app_muslim.surah_yasin.feature.community.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.community.model.*
import kotlin.math.*
import java.time.LocalDate

/**
 * Prayer Analytics Charts Components
 * Comprehensive visualization for prayer statistics, trends, and analytics
 */

// Main Analytics Dashboard
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrayerAnalyticsDashboard(
    dailyAnalytics: List<DailyPrayerAnalytics>,
    weeklyAnalytics: List<WeeklyPrayerAnalytics>,
    countryStats: List<CountryPrayerStats>,
    globalStats: GlobalPrayerStats,
    selectedTimeframe: AnalyticsTimeframe = AnalyticsTimeframe.DAILY,
    onTimeframeChanged: (AnalyticsTimeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Analytics header with timeframe selector
        AnalyticsHeader(
            globalStats = globalStats,
            selectedTimeframe = selectedTimeframe,
            onTimeframeChanged = onTimeframeChanged
        )
        
        // Main analytics content
        AnimatedContent(
            targetState = selectedTimeframe,
            transitionSpec = {
                slideInHorizontally { it } with slideOutHorizontally { -it }
            }
        ) { timeframe ->
            when (timeframe) {
                AnalyticsTimeframe.DAILY -> {
                    DailyAnalyticsView(
                        dailyAnalytics = dailyAnalytics,
                        globalStats = globalStats
                    )
                }
                AnalyticsTimeframe.WEEKLY -> {
                    WeeklyAnalyticsView(
                        weeklyAnalytics = weeklyAnalytics,
                        globalStats = globalStats
                    )
                }
                AnalyticsTimeframe.GEOGRAPHIC -> {
                    GeographicAnalyticsView(
                        countryStats = countryStats,
                        globalStats = globalStats
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsHeader(
    globalStats: GlobalPrayerStats,
    selectedTimeframe: AnalyticsTimeframe,
    onTimeframeChanged: (AnalyticsTimeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 Prayer Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${globalStats.totalPrayersToday} prayers today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        TrendIndicator(trend = globalStats.globalTrend)
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Analytics",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timeframe selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AnalyticsTimeframe.values()) { timeframe ->
                    FilterChip(
                        onClick = { onTimeframeChanged(timeframe) },
                        label = {
                            Text(
                                text = timeframe.displayName,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        selected = selectedTimeframe == timeframe,
                        leadingIcon = {
                            Icon(
                                imageVector = timeframe.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendIndicator(
    trend: TrendDirection,
    modifier: Modifier = Modifier
) {
    val (icon, color, text) = when (trend) {
        TrendDirection.INCREASING -> Triple(Icons.Default.TrendingUp, Color(0xFF4CAF50), "+")
        TrendDirection.DECREASING -> Triple(Icons.Default.TrendingDown, Color(0xFFF44336), "-")
        TrendDirection.STABLE -> Triple(Icons.Default.TrendingFlat, Color(0xFF9E9E9E), "=")
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = trend.name,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun DailyAnalyticsView(
    dailyAnalytics: List<DailyPrayerAnalytics>,
    globalStats: GlobalPrayerStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily prayer trend line chart
        DailyTrendLineChart(
            dailyData = dailyAnalytics,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        
        // Prayer type breakdown pie chart
        PrayerTypeBreakdownChart(
            prayerTypeData = dailyAnalytics.lastOrNull()?.prayerTypeBreakdown ?: emptyMap(),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
        
        // Daily statistics cards
        DailyStatisticsCards(
            todayStats = dailyAnalytics.lastOrNull(),
            yesterdayStats = dailyAnalytics.getOrNull(dailyAnalytics.size - 2)
        )
    }
}

@Composable
private fun WeeklyAnalyticsView(
    weeklyAnalytics: List<WeeklyPrayerAnalytics>,
    globalStats: GlobalPrayerStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weekly trend bar chart
        WeeklyTrendBarChart(
            weeklyData = weeklyAnalytics,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
        
        // Growth rate chart
        GrowthRateChart(
            weeklyData = weeklyAnalytics,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        
        // Weekly summary cards
        WeeklyStatisticsCards(
            currentWeek = weeklyAnalytics.lastOrNull(),
            previousWeek = weeklyAnalytics.getOrNull(weeklyAnalytics.size - 2)
        )
    }
}

@Composable
private fun GeographicAnalyticsView(
    countryStats: List<CountryPrayerStats>,
    globalStats: GlobalPrayerStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top countries horizontal bar chart
        TopCountriesBarChart(
            countryStats = countryStats.take(10),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        
        // Regional distribution donut chart
        RegionalDistributionChart(
            countryStats = countryStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
        
        // Geographic summary
        GeographicSummaryCards(
            totalCountries = countryStats.size,
            topCountry = countryStats.firstOrNull(),
            globalStats = globalStats
        )
    }
}

@Composable
private fun DailyTrendLineChart(
    dailyData: List<DailyPrayerAnalytics>,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = EaseInOutCubic)
    )
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📈 Daily Prayer Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                if (dailyData.isNotEmpty()) {
                    drawDailyTrendLine(dailyData, animatedProgress)
                }
            }
        }
    }
}

private fun DrawScope.drawDailyTrendLine(
    data: List<DailyPrayerAnalytics>,
    progress: Float
) {
    val maxPrayers = data.maxOfOrNull { it.totalPrayers }?.toFloat() ?: 1f
    val chartWidth = size.width - 80f
    val chartHeight = size.height - 60f
    val stepX = chartWidth / (data.size - 1).coerceAtLeast(1)
    
    val lineColor = Color(0xFF4CAF50)
    val gradientColor = Brush.verticalGradient(
        colors = listOf(
            lineColor.copy(alpha = 0.3f),
            Color.Transparent
        )
    )
    
    val path = Path()
    val gradientPath = Path()
    
    data.forEachIndexed { index, dailyStats ->
        val x = 40f + index * stepX
        val y = chartHeight - (dailyStats.totalPrayers / maxPrayers * chartHeight) + 30f
        
        if (index == 0) {
            path.moveTo(x, y)
            gradientPath.moveTo(x, chartHeight + 30f)
            gradientPath.lineTo(x, y)
        } else {
            val progressIndex = (index * progress).coerceAtMost(index.toFloat())
            if (progressIndex >= index) {
                path.lineTo(x, y)
                gradientPath.lineTo(x, y)
            }
        }
    }
    
    // Close gradient path
    gradientPath.lineTo(40f + (data.size - 1) * stepX, chartHeight + 30f)
    gradientPath.close()
    
    // Draw gradient fill
    drawPath(
        path = gradientPath,
        brush = gradientColor
    )
    
    // Draw line
    drawPath(
        path = path,
        color = lineColor,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )
    
    // Draw data points
    data.forEachIndexed { index, dailyStats ->
        if (index * progress <= index) {
            val x = 40f + index * stepX
            val y = chartHeight - (dailyStats.totalPrayers / maxPrayers * chartHeight) + 30f
            
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun PrayerTypeBreakdownChart(
    prayerTypeData: Map<CommunityPrayerType, Long>,
    modifier: Modifier = Modifier
) {
    val animatedValues = remember(prayerTypeData) {
        prayerTypeData.values.map { Animatable(0f) }
    }
    
    LaunchedEffect(prayerTypeData) {
        animatedValues.forEachIndexed { index, animatable ->
            val targetValue = prayerTypeData.values.elementAtOrNull(index)?.toFloat() ?: 0f
            animatable.animateTo(
                targetValue = targetValue,
                animationSpec = tween(1000 + index * 100, easing = EaseOutBack)
            )
        }
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🤲 Prayer Type Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (prayerTypeData.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pie chart
                    Canvas(
                        modifier = Modifier
                            .size(180.dp)
                            .padding(16.dp)
                    ) {
                        drawPrayerTypePieChart(prayerTypeData, animatedValues)
                    }
                    
                    // Legend
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        prayerTypeData.entries.forEachIndexed { index, (prayerType, count) ->
                            PrayerTypeLegendItem(
                                prayerType = prayerType,
                                count = count,
                                color = getPrayerTypeColor(prayerType),
                                percentage = (count.toFloat() / prayerTypeData.values.sum() * 100f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPrayerTypePieChart(
    data: Map<CommunityPrayerType, Long>,
    animatedValues: List<Animatable<Float, AnimationVector1D>>
) {
    val total = data.values.sum().toFloat()
    if (total == 0f) return
    
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = minOf(size.width, size.height) / 2f - 20f
    
    var currentAngle = -90f
    
    data.entries.forEachIndexed { index, (prayerType, count) ->
        val animatedCount = animatedValues.getOrNull(index)?.value ?: count.toFloat()
        val sweepAngle = (animatedCount / total) * 360f
        val color = getPrayerTypeColor(prayerType)
        
        drawArc(
            color = color,
            startAngle = currentAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
        
        currentAngle += sweepAngle
    }
    
    // Draw center circle for donut effect
    drawCircle(
        color = Color.White,
        radius = radius * 0.5f,
        center = center
    )
}

@Composable
private fun PrayerTypeLegendItem(
    prayerType: CommunityPrayerType,
    count: Long,
    color: Color,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        
        Text(
            text = prayerType.displayName,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = "${percentage.toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getPrayerTypeColor(prayerType: CommunityPrayerType): Color {
    return when (prayerType) {
        CommunityPrayerType.TAHLIL -> Color(0xFF4CAF50)
        CommunityPrayerType.YASIN -> Color(0xFF2196F3)
        CommunityPrayerType.FATIHAH -> Color(0xFFFF9800)
        CommunityPrayerType.DHIKR -> Color(0xFF9C27B0)
        CommunityPrayerType.DUA -> Color(0xFFE91E63)
        CommunityPrayerType.QURAN -> Color(0xFF00BCD4)
        CommunityPrayerType.COMMUNITY_PRAYER -> Color(0xFF795548)
    }
}

// Additional helper components
@Composable
private fun DailyStatisticsCards(
    todayStats: DailyPrayerAnalytics?,
    yesterdayStats: DailyPrayerAnalytics?,
    modifier: Modifier = Modifier
) {
    if (todayStats != null) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                StatCard(
                    title = "Today's Prayers",
                    value = "${todayStats.totalPrayers}",
                    change = yesterdayStats?.let { 
                        ((todayStats.totalPrayers - it.totalPrayers).toFloat() / it.totalPrayers * 100f).toInt() 
                    },
                    icon = "🤲"
                )
            }
            
            item {
                StatCard(
                    title = "Participants",
                    value = "${todayStats.uniqueParticipants}",
                    change = yesterdayStats?.let {
                        ((todayStats.uniqueParticipants - it.uniqueParticipants).toFloat() / it.uniqueParticipants * 100f).toInt()
                    },
                    icon = "👥"
                )
            }
            
            item {
                StatCard(
                    title = "Avg Duration",
                    value = "${todayStats.averageSessionDuration.toInt()}m",
                    change = yesterdayStats?.let {
                        ((todayStats.averageSessionDuration - it.averageSessionDuration) / it.averageSessionDuration * 100f).toInt()
                    },
                    icon = "⏱️"
                )
            }
            
            item {
                StatCard(
                    title = "Active Regions",
                    value = "${todayStats.regionsActive}",
                    change = yesterdayStats?.let {
                        todayStats.regionsActive - it.regionsActive
                    },
                    icon = "🌍"
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    change: Int?,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            change?.let { changeValue ->
                val changeText = if (changeValue > 0) "+$changeValue%" else "$changeValue%"
                val changeColor = if (changeValue > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = changeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Timeframe enum for analytics
enum class AnalyticsTimeframe(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    DAILY("Daily", Icons.Default.CalendarToday),
    WEEKLY("Weekly", Icons.Default.CalendarViewWeek),
    GEOGRAPHIC("Geographic", Icons.Default.Public)
}

// Additional stub implementations for weekly and geographic charts
@Composable
private fun WeeklyTrendBarChart(
    weeklyData: List<WeeklyPrayerAnalytics>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Weekly Bar Chart - Implementation Ready")
        }
    }
}

@Composable
private fun GrowthRateChart(
    weeklyData: List<WeeklyPrayerAnalytics>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Growth Rate Chart - Implementation Ready")
        }
    }
}

@Composable
private fun WeeklyStatisticsCards(
    currentWeek: WeeklyPrayerAnalytics?,
    previousWeek: WeeklyPrayerAnalytics?,
    modifier: Modifier = Modifier
) {
    // Implementation similar to DailyStatisticsCards
}

@Composable
private fun TopCountriesBarChart(
    countryStats: List<CountryPrayerStats>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Top Countries Chart - Implementation Ready")
        }
    }
}

@Composable
private fun RegionalDistributionChart(
    countryStats: List<CountryPrayerStats>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Regional Distribution Chart - Implementation Ready")
        }
    }
}

@Composable
private fun GeographicSummaryCards(
    totalCountries: Int,
    topCountry: CountryPrayerStats?,
    globalStats: GlobalPrayerStats,
    modifier: Modifier = Modifier
) {
    // Implementation for geographic summary cards
}

// Preview Parameter Providers
class PrayerAnalyticsProvider : PreviewParameterProvider<Triple<List<DailyPrayerAnalytics>, List<WeeklyPrayerAnalytics>, GlobalPrayerStats>> {
    override val values = sequenceOf(
        // Regular analytics data
        Triple(
            listOf(
                DailyPrayerAnalytics(
                    date = java.time.LocalDate.now().minusDays(6).toString(),
                    totalPrayers = 45_000L,
                    uniqueParticipants = 8_500L,
                    averageSessionDuration = 12.5,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 18_000L,
                        CommunityPrayerType.YASIN to 15_000L,
                        CommunityPrayerType.FATIHAH to 12_000L
                    ),
                    peakHour = 19,
                    regionsActive = 85,
                    newMemorials = 45L,
                    completedSessions = 8_200L
                ),
                DailyPrayerAnalytics(
                    date = LocalDate.now().minusDays(5).toString(),
                    totalPrayers = 47_500L,
                    uniqueParticipants = 8_800L,
                    averageSessionDuration = 13.2,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 19_000L,
                        CommunityPrayerType.YASIN to 16_000L,
                        CommunityPrayerType.FATIHAH to 12_500L
                    ),
                    peakHour = 20,
                    regionsActive = 88,
                    newMemorials = 52L,
                    completedSessions = 8_650L
                ),
                DailyPrayerAnalytics(
                    date = LocalDate.now().minusDays(4).toString(),
                    totalPrayers = 52_000L,
                    uniqueParticipants = 9_200L,
                    averageSessionDuration = 14.1,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 21_000L,
                        CommunityPrayerType.YASIN to 17_500L,
                        CommunityPrayerType.FATIHAH to 13_500L
                    ),
                    regionsActive = 92
                ),
                DailyPrayerAnalytics(
                    date = LocalDate.now().minusDays(3).toString(),
                    totalPrayers = 49_000L,
                    uniqueParticipants = 8_900L,
                    averageSessionDuration = 13.8,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 19_500L,
                        CommunityPrayerType.YASIN to 16_500L,
                        CommunityPrayerType.FATIHAH to 13_000L
                    ),
                    regionsActive = 89
                ),
                DailyPrayerAnalytics(
                    date = LocalDate.now().minusDays(2).toString(),
                    totalPrayers = 55_000L,
                    uniqueParticipants = 9_800L,
                    averageSessionDuration = 15.2,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 22_000L,
                        CommunityPrayerType.YASIN to 18_500L,
                        CommunityPrayerType.FATIHAH to 14_500L
                    ),
                    regionsActive = 95
                ),
                DailyPrayerAnalytics(
                    date = LocalDate.now().minusDays(1).toString(),
                    totalPrayers = 58_500L,
                    uniqueParticipants = 10_200L,
                    averageSessionDuration = 16.1,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 23_500L,
                        CommunityPrayerType.YASIN to 19_500L,
                        CommunityPrayerType.FATIHAH to 15_500L
                    ),
                    regionsActive = 98
                ),
                DailyPrayerAnalytics(
                    date = LocalDate.now().toString(),
                    totalPrayers = 62_000L,
                    uniqueParticipants = 11_000L,
                    averageSessionDuration = 17.3,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 25_000L,
                        CommunityPrayerType.YASIN to 20_500L,
                        CommunityPrayerType.FATIHAH to 16_500L
                    ),
                    regionsActive = 102
                )
            ),
            listOf(
                WeeklyPrayerAnalytics(
                    weekStartDate = LocalDate.now().minusDays(14).toString(),
                    totalPrayers = 280_000L,
                    uniqueParticipants = 52_000L,
                    averageDailyPrayers = 40000.0,
                    growthRate = 8.5f
                ),
                WeeklyPrayerAnalytics(
                    weekStartDate = LocalDate.now().minusDays(7).toString(),
                    totalPrayers = 320_000L,
                    uniqueParticipants = 58_000L,
                    averageDailyPrayers = 45700.0,
                    growthRate = 14.3f
                ),
                WeeklyPrayerAnalytics(
                    weekStartDate = LocalDate.now().toString(),
                    totalPrayers = 385_000L,
                    uniqueParticipants = 65_500L,
                    averageDailyPrayers = 55000.0,
                    growthRate = 20.3f
                )
            ),
            GlobalPrayerStats(
                totalPrayersToday = 62_000L,
                totalActivePrayers = 25_000_000L,
                totalParticipants = 850_000L,
                totalMemorials = 11_000L,
                activeRegions = 157,
                globalTrend = TrendDirection.INCREASING,
                peakHour = 20,
                topPrayerType = "Tahlil"
            )
        ),
        
        // High volume data
        Triple(
            listOf(
                DailyPrayerAnalytics(
                    date = LocalDate.now().toString(),
                    totalPrayers = 125_000L,
                    uniqueParticipants = 28_500L,
                    averageSessionDuration = 22.1,
                    prayerTypeBreakdown = mapOf(
                        CommunityPrayerType.TAHLIL to 45_000L,
                        CommunityPrayerType.YASIN to 38_000L,
                        CommunityPrayerType.FATIHAH to 25_000L,
                        CommunityPrayerType.DHIKR to 12_000L,
                        CommunityPrayerType.DUA to 5_000L
                    ),
                    regionsActive = 195
                )
            ),
            emptyList(),
            GlobalPrayerStats(
                totalPrayersToday = 125_000L,
                totalActivePrayers = 50_000_000L,
                totalParticipants = 1_200_000L,
                totalMemorials = 28_500L,
                activeRegions = 195,
                globalTrend = TrendDirection.INCREASING,
                peakHour = 21,
                topPrayerType = "Tahlil"
            )
        )
    )
}

class DailyAnalyticsProvider : PreviewParameterProvider<List<DailyPrayerAnalytics>> {
    override val values = sequenceOf(
        // Ramadan special period (high activity)
        listOf(
            DailyPrayerAnalytics(
                date = LocalDate.now().minusDays(2).toString(),
                totalPrayers = 95_000L,
                uniqueParticipants = 18_500L,
                averageSessionDuration = 25.2,
                prayerTypeBreakdown = mapOf(
                    CommunityPrayerType.TAHLIL to 35_000L,
                    CommunityPrayerType.YASIN to 30_000L,
                    CommunityPrayerType.FATIHAH to 20_000L,
                    CommunityPrayerType.DHIKR to 10_000L
                ),
                regionsActive = 178
            ),
            DailyPrayerAnalytics(
                date = LocalDate.now().minusDays(1).toString(),
                totalPrayers = 108_000L,
                uniqueParticipants = 21_000L,
                averageSessionDuration = 28.5,
                prayerTypeBreakdown = mapOf(
                    CommunityPrayerType.TAHLIL to 40_000L,
                    CommunityPrayerType.YASIN to 35_000L,
                    CommunityPrayerType.FATIHAH to 23_000L,
                    CommunityPrayerType.DHIKR to 10_000L
                ),
                regionsActive = 185
            ),
            DailyPrayerAnalytics(
                date = LocalDate.now().toString(),
                totalPrayers = 125_000L,
                uniqueParticipants = 24_500L,
                averageSessionDuration = 31.8,
                prayerTypeBreakdown = mapOf(
                    CommunityPrayerType.TAHLIL to 48_000L,
                    CommunityPrayerType.YASIN to 40_000L,
                    CommunityPrayerType.FATIHAH to 25_000L,
                    CommunityPrayerType.DHIKR to 12_000L
                ),
                regionsActive = 192
            )
        ),
        
        // Regular weekday period
        listOf(
            DailyPrayerAnalytics(
                date = LocalDate.now().toString(),
                totalPrayers = 45_000L,
                uniqueParticipants = 8_500L,
                averageSessionDuration = 15.2,
                prayerTypeBreakdown = mapOf(
                    CommunityPrayerType.TAHLIL to 18_000L,
                    CommunityPrayerType.YASIN to 15_000L,
                    CommunityPrayerType.FATIHAH to 12_000L
                ),
                regionsActive = 125
            )
        )
    )
}

class PrayerTypeBreakdownProvider : PreviewParameterProvider<Map<CommunityPrayerType, Long>> {
    override val values = sequenceOf(
        // Balanced distribution
        mapOf(
            CommunityPrayerType.TAHLIL to 25_000L,
            CommunityPrayerType.YASIN to 20_000L,
            CommunityPrayerType.FATIHAH to 15_000L,
            CommunityPrayerType.DHIKR to 8_000L,
            CommunityPrayerType.DUA to 5_000L
        ),
        
        // Tahlil dominant
        mapOf(
            CommunityPrayerType.TAHLIL to 45_000L,
            CommunityPrayerType.YASIN to 12_000L,
            CommunityPrayerType.FATIHAH to 8_000L,
            CommunityPrayerType.DHIKR to 3_000L
        ),
        
        // Three-way split
        mapOf(
            CommunityPrayerType.TAHLIL to 22_000L,
            CommunityPrayerType.YASIN to 21_000L,
            CommunityPrayerType.FATIHAH to 20_000L
        )
    )
}

// Preview Composables
@Preview(name = "Prayer Analytics Dashboard - Daily View")
@Composable
fun PrayerAnalyticsDashboardDailyPreview(
    @PreviewParameter(PrayerAnalyticsProvider::class) 
    data: Triple<List<DailyPrayerAnalytics>, List<WeeklyPrayerAnalytics>, GlobalPrayerStats>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = data.first,
                weeklyAnalytics = data.second,
                countryStats = emptyList(),
                globalStats = data.third,
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Prayer Analytics Dashboard - Weekly View")
@Composable
fun PrayerAnalyticsDashboardWeeklyPreview() {
    val globalStats = GlobalPrayerStats(
        totalPrayersToday = 95_000L,
        totalActivePrayers = 38_500_000L,
        totalParticipants = 975_000L,
        totalMemorials = 18_500L,
        activeRegions = 172,
        globalTrend = TrendDirection.INCREASING,
        peakHour = 19,
        topPrayerType = "Yasin"
    )
    
    val weeklyAnalytics = listOf(
        WeeklyPrayerAnalytics(
            weekStartDate = LocalDate.now().minusDays(21).toString(),
            totalPrayers = 450_000L,
            uniqueParticipants = 85_000L,
            averageDailyPrayers = 64285.0,
            growthRate = 12.5f
        ),
        WeeklyPrayerAnalytics(
            weekStartDate = LocalDate.now().minusDays(14).toString(),
            totalPrayers = 520_000L,
            uniqueParticipants = 92_000L,
            averageDailyPrayers = 74285.0,
            growthRate = 15.6f
        ),
        WeeklyPrayerAnalytics(
            weekStartDate = LocalDate.now().minusDays(7).toString(),
            totalPrayers = 625_000L,
            uniqueParticipants = 105_000L,
            averageDailyPrayers = 89285.0,
            growthRate = 20.2f
        )
    )
    
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = emptyList(),
                weeklyAnalytics = weeklyAnalytics,
                countryStats = emptyList(),
                globalStats = globalStats,
                selectedTimeframe = AnalyticsTimeframe.WEEKLY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Prayer Analytics Dashboard - Geographic View")
@Composable
fun PrayerAnalyticsDashboardGeographicPreview() {
    val countryStats = listOf(
        CountryPrayerStats(
            countryCode = "ID",
            countryName = "Indonesia",
            totalPrayers = 5_200_000L,
            activeParticipants = 850_000L,
            flag = "🇮🇩",
            heatLevel = 1.0f,
            rank = 1
        ),
        CountryPrayerStats(
            countryCode = "PK",
            countryName = "Pakistan",
            totalPrayers = 3_800_000L,
            activeParticipants = 625_000L,
            flag = "🇵🇰",
            heatLevel = 0.93f,
            rank = 2
        ),
        CountryPrayerStats(
            countryCode = "BD",
            countryName = "Bangladesh",
            totalPrayers = 2_950_000L,
            activeParticipants = 485_000L,
            flag = "🇧🇩",
            heatLevel = 0.87f,
            rank = 3
        ),
        CountryPrayerStats(
            countryCode = "SA",
            countryName = "Saudi Arabia",
            totalPrayers = 1_850_000L,
            activeParticipants = 325_000L,
            flag = "🇸🇦",
            heatLevel = 0.78f,
            rank = 4
        ),
        CountryPrayerStats(
            countryCode = "TR",
            countryName = "Turkey",
            totalPrayers = 1_425_000L,
            activeParticipants = 245_000L,
            flag = "🇹🇷",
            heatLevel = 0.72f,
            rank = 5
        )
    )
    
    val globalStats = GlobalPrayerStats(
        totalPrayersToday = 125_000L,
        totalActivePrayers = 45_000_000L,
        totalParticipants = 1_150_000L,
        totalMemorials = 28_500L,
        activeRegions = 185,
        globalTrend = TrendDirection.INCREASING,
        peakHour = 20,
        topPrayerType = "Tahlil"
    )
    
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = emptyList(),
                weeklyAnalytics = emptyList(),
                countryStats = countryStats,
                globalStats = globalStats,
                selectedTimeframe = AnalyticsTimeframe.GEOGRAPHIC,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Daily Analytics View")
@Composable
fun DailyAnalyticsViewPreview(
    @PreviewParameter(DailyAnalyticsProvider::class) 
    dailyAnalytics: List<DailyPrayerAnalytics>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DailyAnalyticsView(
                dailyAnalytics = dailyAnalytics,
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 125_000L,
                    totalActivePrayers = 42_000_000L,
                    totalParticipants = 1_050_000L,
                    totalMemorials = 24_500L,
                    activeRegions = 178,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 21,
                    topPrayerType = "Tahlil"
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Daily Trend Line Chart")
@Composable
fun DailyTrendLineChartPreview() {
    val dailyData = listOf(
        DailyPrayerAnalytics(
            date = LocalDate.now().minusDays(6).toString(),
            totalPrayers = 42_000L,
            uniqueParticipants = 8_200L,
            averageSessionDuration = 14.5
        ),
        DailyPrayerAnalytics(
            date = LocalDate.now().minusDays(5).toString(),
            totalPrayers = 48_500L,
            uniqueParticipants = 9_100L,
            averageSessionDuration = 15.8
        ),
        DailyPrayerAnalytics(
            date = LocalDate.now().minusDays(4).toString(),
            totalPrayers = 52_000L,
            uniqueParticipants = 9_800L,
            averageSessionDuration = 16.2
        ),
        DailyPrayerAnalytics(
            date = LocalDate.now().minusDays(3).toString(),
            totalPrayers = 55_500L,
            uniqueParticipants = 10_200L,
            averageSessionDuration = 17.1
        ),
        DailyPrayerAnalytics(
            date = LocalDate.now().minusDays(2).toString(),
            totalPrayers = 58_000L,
            uniqueParticipants = 10_800L,
            averageSessionDuration = 18.5
        ),
        DailyPrayerAnalytics(
            date = LocalDate.now().minusDays(1).toString(),
            totalPrayers = 61_500L,
            uniqueParticipants = 11_500L,
            averageSessionDuration = 19.2
        ),
        DailyPrayerAnalytics(
            date = LocalDate.now().toString(),
            totalPrayers = 67_000L,
            uniqueParticipants = 12_800L,
            averageSessionDuration = 21.5
        )
    )
    
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DailyTrendLineChart(
                dailyData = dailyData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(name = "Prayer Type Breakdown Chart")
@Composable
fun PrayerTypeBreakdownChartPreview(
    @PreviewParameter(PrayerTypeBreakdownProvider::class) 
    prayerTypeData: Map<CommunityPrayerType, Long>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerTypeBreakdownChart(
                prayerTypeData = prayerTypeData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(name = "Prayer Type Legend Item")
@Composable
fun PrayerTypeLegendItemPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Prayer Type Legend Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                PrayerTypeLegendItem(
                    prayerType = CommunityPrayerType.TAHLIL,
                    count = 25_000L,
                    color = getPrayerTypeColor(CommunityPrayerType.TAHLIL),
                    percentage = 40.0f
                )
                
                PrayerTypeLegendItem(
                    prayerType = CommunityPrayerType.YASIN,
                    count = 20_000L,
                    color = getPrayerTypeColor(CommunityPrayerType.YASIN),
                    percentage = 32.0f
                )
                
                PrayerTypeLegendItem(
                    prayerType = CommunityPrayerType.FATIHAH,
                    count = 15_000L,
                    color = getPrayerTypeColor(CommunityPrayerType.FATIHAH),
                    percentage = 24.0f
                )
                
                PrayerTypeLegendItem(
                    prayerType = CommunityPrayerType.DHIKR,
                    count = 2_500L,
                    color = getPrayerTypeColor(CommunityPrayerType.DHIKR),
                    percentage = 4.0f
                )
            }
        }
    }
}

@Preview(name = "Statistics Cards")
@Composable
fun DailyStatisticsCardsPreview() {
    val todayStats = DailyPrayerAnalytics(
        date = LocalDate.now().toString(),
        totalPrayers = 67_500L,
        uniqueParticipants = 12_800L,
        averageSessionDuration = 21.5,
        prayerTypeBreakdown = mapOf(
            CommunityPrayerType.TAHLIL to 27_000L,
            CommunityPrayerType.YASIN to 22_500L,
            CommunityPrayerType.FATIHAH to 18_000L
        ),
        regionsActive = 145
    )
    
    val yesterdayStats = DailyPrayerAnalytics(
        date = LocalDate.now().minusDays(1).toString(),
        totalPrayers = 61_500L,
        uniqueParticipants = 11_500L,
        averageSessionDuration = 19.2,
        regionsActive = 138
    )
    
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DailyStatisticsCards(
                todayStats = todayStats,
                yesterdayStats = yesterdayStats,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Stat Card")
@Composable
fun StatCardPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                StatCard(
                    title = "Today's Prayers",
                    value = "67,500",
                    change = 15, // +15%
                    icon = "🤲"
                )
                
                StatCard(
                    title = "Participants",
                    value = "12,800",
                    change = 11, // +11%
                    icon = "👥"
                )
                
                StatCard(
                    title = "Avg Duration",
                    value = "21m",
                    change = -2, // -2%
                    icon = "⏱️"
                )
                
                StatCard(
                    title = "Active Regions",
                    value = "145",
                    change = 7, // +7 regions
                    icon = "🌍"
                )
            }
        }
    }
}

@Preview(name = "Analytics Header")
@Composable
fun AnalyticsHeaderPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AnalyticsHeader(
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 125_000L,
                    totalActivePrayers = 45_000_000L,
                    totalParticipants = 1_150_000L,
                    totalMemorials = 24_500L,
                    activeRegions = 185,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 20,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Trend Indicators")
@Composable
fun TrendIndicatorPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Trend Indicators",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrendIndicator(trend = TrendDirection.INCREASING)
                    Text("Increasing")
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrendIndicator(trend = TrendDirection.DECREASING)
                    Text("Decreasing")
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrendIndicator(trend = TrendDirection.STABLE)
                    Text("Stable")
                }
            }
        }
    }
}

// ===============================================
// Device-Specific & Advanced Previews
// ===============================================

// 1. FOLDABLE DEVICE PREVIEWS

@Preview(
    name = "Analytics Dashboard - Foldable Open",
    device = "spec:width=2208dp,height=1768dp,dpi=420",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardFoldableOpenPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 125_000L,
                        uniqueParticipants = 28_500L,
                        averageSessionDuration = 22.1,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 45_000L,
                            CommunityPrayerType.YASIN to 38_000L,
                            CommunityPrayerType.FATIHAH to 25_000L,
                            CommunityPrayerType.DHIKR to 12_000L,
                            CommunityPrayerType.DUA to 5_000L
                        ),
                        regionsActive = 195
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 125_000L,
                    totalActivePrayers = 50_000_000L,
                    totalParticipants = 1_200_000L,
                    totalMemorials = 28_500L,
                    activeRegions = 195,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 21,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Preview(
    name = "Analytics Dashboard - Foldable Closed",
    device = "spec:width=841dp,height=1768dp,dpi=420",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardFoldableClosedPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 67_500L,
                        uniqueParticipants = 12_800L,
                        averageSessionDuration = 21.5,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 27_000L,
                            CommunityPrayerType.YASIN to 22_500L,
                            CommunityPrayerType.FATIHAH to 18_000L
                        ),
                        regionsActive = 145
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 67_500L,
                    totalActivePrayers = 42_000_000L,
                    totalParticipants = 1_050_000L,
                    totalMemorials = 24_500L,
                    activeRegions = 178,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 21,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// 2. HIGH CONTRAST ACCESSIBILITY PREVIEWS

@Preview(
    name = "Analytics Dashboard - High Contrast",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardHighContrastPreview() {
    TahlilTheme {
        Surface(
            color = Color.Black,
            modifier = Modifier.background(Color.Black)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides Color.White
            ) {
                PrayerAnalyticsDashboard(
                    dailyAnalytics = listOf(
                        DailyPrayerAnalytics(
                            date = LocalDate.now().toString(),
                            totalPrayers = 95_000L,
                            uniqueParticipants = 18_500L,
                            averageSessionDuration = 25.2,
                            prayerTypeBreakdown = mapOf(
                                CommunityPrayerType.TAHLIL to 35_000L,
                                CommunityPrayerType.YASIN to 30_000L,
                                CommunityPrayerType.FATIHAH to 20_000L,
                                CommunityPrayerType.DHIKR to 10_000L
                            ),
                            regionsActive = 178
                        )
                    ),
                    weeklyAnalytics = emptyList(),
                    countryStats = emptyList(),
                    globalStats = GlobalPrayerStats(
                        totalPrayersToday = 95_000L,
                        totalActivePrayers = 38_500_000L,
                        totalParticipants = 975_000L,
                        totalMemorials = 18_500L,
                        activeRegions = 172,
                        globalTrend = TrendDirection.INCREASING,
                        peakHour = 19,
                        topPrayerType = "Yasin"
                    ),
                    selectedTimeframe = AnalyticsTimeframe.DAILY,
                    onTimeframeChanged = { },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// 3. ISLAMIC COLOR SCHEME PREVIEWS

@Preview(
    name = "Analytics Dashboard - Islamic Green Theme",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardIslamicGreenPreview() {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF0D7377), // Islamic Teal
            secondary = Color(0xFF14A085), // Islamic Green
            tertiary = Color(0xFF41A58D), // Light Green
            surface = Color(0xFFF8FBF8), // Light Green Background
            background = Color(0xFFF0F8F0), // Very Light Green
            surfaceVariant = Color(0xFFE8F5E8) // Green Tinted Surface
        )
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 85_000L,
                        uniqueParticipants = 16_200L,
                        averageSessionDuration = 23.5,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 32_000L,
                            CommunityPrayerType.YASIN to 28_000L,
                            CommunityPrayerType.FATIHAH to 20_000L,
                            CommunityPrayerType.DHIKR to 5_000L
                        ),
                        regionsActive = 165
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 85_000L,
                    totalActivePrayers = 35_000_000L,
                    totalParticipants = 895_000L,
                    totalMemorials = 16_200L,
                    activeRegions = 165,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 18,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(
    name = "Analytics Dashboard - Islamic Gold Theme",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardIslamicGoldPreview() {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFFD4AF37), // Islamic Gold
            secondary = Color(0xFFB8860B), // Dark Gold
            tertiary = Color(0xFFDAA520), // Goldenrod
            surface = Color(0xFFFFFDF5), // Cream Background
            background = Color(0xFFFFFAF0), // Floral White
            surfaceVariant = Color(0xFFFFF8DC) // Cornsilk
        )
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 112_000L,
                        uniqueParticipants = 22_800L,
                        averageSessionDuration = 26.8,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 42_000L,
                            CommunityPrayerType.YASIN to 35_000L,
                            CommunityPrayerType.FATIHAH to 25_000L,
                            CommunityPrayerType.DHIKR to 10_000L
                        ),
                        regionsActive = 188
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 112_000L,
                    totalActivePrayers = 44_000_000L,
                    totalParticipants = 1_100_000L,
                    totalMemorials = 22_800L,
                    activeRegions = 188,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 20,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(
    name = "Analytics Dashboard - Traditional Islamic Theme",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardTraditionalIslamicPreview() {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF8B4513), // Saddle Brown (Traditional Islamic)
            secondary = Color(0xFFCD853F), // Peru
            tertiary = Color(0xFFDEB887), // Burlywood
            surface = Color(0xFFFAF0E6), // Linen
            background = Color(0xFFF5F5DC), // Beige
            surfaceVariant = Color(0xFFDDD3C0) // Warm Gray
        )
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 78_000L,
                        uniqueParticipants = 14_500L,
                        averageSessionDuration = 28.2,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 35_000L,
                            CommunityPrayerType.YASIN to 25_000L,
                            CommunityPrayerType.FATIHAH to 18_000L
                        ),
                        regionsActive = 152
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 78_000L,
                    totalActivePrayers = 32_000_000L,
                    totalParticipants = 820_000L,
                    totalMemorials = 14_500L,
                    activeRegions = 152,
                    globalTrend = TrendDirection.STABLE,
                    peakHour = 19,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// 4. FONT SCALING ACCESSIBILITY PREVIEWS

@Preview(
    name = "Analytics Dashboard - Font Scale 150%",
    fontScale = 1.5f,
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardFontScale150Preview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 65_000L,
                        uniqueParticipants = 12_500L,
                        averageSessionDuration = 20.8,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 28_000L,
                            CommunityPrayerType.YASIN to 22_000L,
                            CommunityPrayerType.FATIHAH to 15_000L
                        ),
                        regionsActive = 142
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 65_000L,
                    totalActivePrayers = 38_000_000L,
                    totalParticipants = 925_000L,
                    totalMemorials = 12_500L,
                    activeRegions = 142,
                    globalTrend = TrendDirection.INCREASING,
                    peakHour = 19,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Preview(
    name = "Analytics Dashboard - Font Scale 200%",
    fontScale = 2.0f,
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardFontScale200Preview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PrayerAnalyticsDashboard(
                dailyAnalytics = listOf(
                    DailyPrayerAnalytics(
                        date = LocalDate.now().toString(),
                        totalPrayers = 58_000L,
                        uniqueParticipants = 11_200L,
                        averageSessionDuration = 18.5,
                        prayerTypeBreakdown = mapOf(
                            CommunityPrayerType.TAHLIL to 25_000L,
                            CommunityPrayerType.YASIN to 20_000L,
                            CommunityPrayerType.FATIHAH to 13_000L
                        ),
                        regionsActive = 128
                    )
                ),
                weeklyAnalytics = emptyList(),
                countryStats = emptyList(),
                globalStats = GlobalPrayerStats(
                    totalPrayersToday = 58_000L,
                    totalActivePrayers = 34_000_000L,
                    totalParticipants = 825_000L,
                    totalMemorials = 11_200L,
                    activeRegions = 128,
                    globalTrend = TrendDirection.STABLE,
                    peakHour = 18,
                    topPrayerType = "Tahlil"
                ),
                selectedTimeframe = AnalyticsTimeframe.DAILY,
                onTimeframeChanged = { },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// 5. NETWORK OFFLINE STATE PREVIEWS

@Preview(
    name = "Analytics Dashboard - Offline State",
    showBackground = true
)
@Composable
fun PrayerAnalyticsDashboardOfflinePreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Offline",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Connection Lost",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your prayers continue to be counted. Analytics will sync when connection is restored.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Continue Praying Offline",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            Text(
                                text = "Your memorial prayers are saved locally",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { /* Retry connection */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry Connection")
                }
            }
        }
    }
}