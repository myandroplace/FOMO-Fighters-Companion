package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.analytics.BattleAnalytics
import com.example.ui.components.MetricStatCard
import com.example.ui.components.formatCompactNumber
import com.example.ui.components.formatNumber
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentGold
import com.example.ui.theme.LossRed
import com.example.ui.theme.LootFood
import com.example.ui.theme.LootGem
import com.example.ui.theme.LootStone
import com.example.ui.theme.LootWood
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.StarGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToBattles: () -> Unit,
    onNavigateToCamps: () -> Unit,
    onShareReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.overallStats.collectAsState()
    val battles by viewModel.battles.collectAsState()
    val campTypeStats by viewModel.campTypeStats.collectAsState()
    val firstBattle = battles.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Player Profile Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_profile_card")
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrimaryViolet.copy(alpha = 0.2f))
                                .border(1.5.dp, PrimaryViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MilitaryTech,
                                contentDescription = "Avatar",
                                tint = PrimaryViolet,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = firstBattle?.attackerName ?: "Bob Marley Money Mining",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Раса: ${firstBattle?.attackerRace ?: "Человек"} • Ур. ${firstBattle?.attackerLevel ?: 11}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AccentGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Сила: ${formatCompactNumber(firstBattle?.attackerPower ?: 41509L)}",
                                        color = AccentGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = onShareReport,
                        modifier = Modifier.testTag("share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Поделиться отчетом",
                            tint = AccentGold
                        )
                    }
                }
            }
        }

        // Financial & Efficiency KPI Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Чистая прибыль",
                    value = "+${formatCompactNumber(stats.netProfit)}",
                    subtitle = "ROI: +${String.format("%.1f", stats.overallRoiPercent)}%",
                    icon = Icons.Filled.TrendingUp,
                    iconColor = ProfitGreen,
                    accentColor = ProfitGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_net_profit"
                )
                MetricStatCard(
                    title = "Потери армии",
                    value = "-${formatCompactNumber(stats.totalTroopLossCost)}",
                    subtitle = "Атака: -${formatCompactNumber(stats.totalLostAtk)}",
                    icon = Icons.Filled.Shield,
                    iconColor = LossRed,
                    accentColor = LossRed,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_total_loss"
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Добыто всего",
                    value = formatCompactNumber(stats.totalGrossLoot),
                    subtitle = "За ${stats.totalBattles} рейдов",
                    icon = Icons.Filled.MonetizationOn,
                    iconColor = AccentGold,
                    accentColor = AccentGold,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_gross_loot"
                )
                MetricStatCard(
                    title = "3★ Эффективность",
                    value = "${stats.star3Count} / ${stats.totalBattles}",
                    subtitle = "${String.format("%.0f", (stats.star3Count.toDouble() / stats.totalBattles.coerceAtLeast(1)) * 100)}% побед без потерь",
                    icon = Icons.Filled.Star,
                    iconColor = StarGold,
                    accentColor = StarGold,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_star_efficiency"
                )
            }
        }

        // Loot Breakdown Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Добытые ресурсы",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    ResourceProgressRow(
                        name = "Еда 🌾",
                        amount = stats.totalFood,
                        total = stats.totalGrossLoot,
                        color = LootFood
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ResourceProgressRow(
                        name = "Дерево 🪵",
                        amount = stats.totalWood,
                        total = stats.totalGrossLoot,
                        color = LootWood
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ResourceProgressRow(
                        name = "Камень 🪨",
                        amount = stats.totalStone,
                        total = stats.totalGrossLoot,
                        color = LootStone
                    )
                    if (stats.totalGem > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        ResourceProgressRow(
                            name = "Алмазы 💎",
                            amount = stats.totalGem,
                            total = stats.totalGrossLoot,
                            color = LootGem
                        )
                    }
                }
            }
        }

        // Hero Progression
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Прокачка полководца",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Опыт за рейды и книги звезд",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "+${formatNumber(stats.totalHeroExp.toLong())} XP",
                                color = PrimaryViolet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentGold.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⭐ ${stats.totalStarExp} книг",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Troop Casualties Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Потери войск (всего)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "-${formatNumber(stats.troopCasualties.values.sum().toLong())} воинов",
                            color = LossRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (stats.troopCasualties.isEmpty()) {
                        Text(
                            text = "Потерь нет! Превосходная тактика.",
                            color = ProfitGreen,
                            fontSize = 13.sp
                        )
                    } else {
                        stats.troopCasualties.forEach { (unitKey, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = BattleAnalytics.getUnitDisplayName(unitKey),
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "-$count",
                                    color = LossRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Camp Profitability Leaderboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Рейтинг доходности целей",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Средняя чистая прибыль с атаки по типам лагерей",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    campTypeStats.take(4).forEach { targetStat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${targetStat.race.replaceFirstChar { it.uppercase() }} Ур. ${targetStat.level}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${targetStat.attackCount} атак • ${targetStat.campsCount} лагерей",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+${formatCompactNumber(targetStat.avgNetPerAttack)} net/атака",
                                    color = ProfitGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Лут: ${formatCompactNumber(targetStat.avgLootPerAttack)}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Navigation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToCamps,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_to_camps_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
                ) {
                    Text("Лагеря и Осады", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onNavigateToBattles,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_to_battles_button")
                ) {
                    Text("Логи боев", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResourceProgressRow(
    name: String,
    amount: Long,
    total: Long,
    color: Color
) {
    val fraction = if (total > 0) (amount.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, color = TextSecondary, fontSize = 13.sp)
            Text(
                text = "${formatCompactNumber(amount)} (${String.format("%.1f", fraction * 100)}%)",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = SurfaceCardBorder
        )
    }
}
