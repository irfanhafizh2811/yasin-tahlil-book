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
import com.app_muslim.surah_yasin.feature.community.model.*
import kotlin.math.*

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