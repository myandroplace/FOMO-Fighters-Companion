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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.analytics.BattleAnalytics
import com.example.ui.components.ResourcePill
import com.example.ui.components.formatCompactNumber
import com.example.ui.components.formatNumber
import com.example.ui.theme.AccentGold
import com.example.ui.theme.LossRed
import com.example.ui.theme.LootFood
import com.example.ui.theme.LootStone
import com.example.ui.theme.LootWood
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CalculatorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedRace by remember { mutableStateOf("skeleton") }
    var selectedLevel by remember { mutableIntStateOf(12) }

    val simulation = viewModel.simulateRaid(selectedRace, selectedLevel)
    val retrainCosts = viewModel.getRetrainCosts()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Тактический Калькулятор",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Прогноз доходности рейдов и расчет переобучения армии",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Raid Simulator vs Troop Retraining
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = PrimaryViolet,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryViolet
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Симулятор Рейда", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Восстановление Армии", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // RAID SIMULATOR TAB
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Selectors Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Выберите цель атаки:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Race Selector
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = selectedRace == "skeleton",
                                    onClick = { selectedRace = "skeleton" },
                                    label = { Text("💀 Скелеты") },
                                    colors = chipColors()
                                )
                                FilterChip(
                                    selected = selectedRace == "goblin",
                                    onClick = { selectedRace = "goblin" },
                                    label = { Text("👺 Гоблины") },
                                    colors = chipColors()
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Уровень лагеря:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Level Selector
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(10, 11, 12).forEach { lvl ->
                                    FilterChip(
                                        selected = selectedLevel == lvl,
                                        onClick = { selectedLevel = lvl },
                                        label = { Text("Уровень $lvl") },
                                        colors = chipColors()
                                    )
                                }
                            }
                        }
                    }
                }

                // Simulation Outcome Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Прогноз по итогам прошлых боев",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ProfitGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "+${formatCompactNumber(simulation.estimatedNetProfit)} чистыми",
                                        color = ProfitGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Ожидаемый общий лут", color = TextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = formatNumber(simulation.estimatedLootTotal),
                                        color = AccentGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Ожидаемые потери армии", color = TextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = "-${formatNumber(simulation.estimatedLossCost)}",
                                        color = LossRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ResourcePill(label = "Food", amount = simulation.estimatedFood, color = LootFood, icon = "🌾")
                                ResourcePill(label = "Wood", amount = simulation.estimatedWood, color = LootWood, icon = "🪵")
                                ResourcePill(label = "Stone", amount = simulation.estimatedStone, color = LootStone, icon = "🪨")
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = SurfaceCardBorder)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Recommended Army Size
                            Text(
                                text = "Рекомендуемая сила атаки (для 3★):",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚔️ не менее ${formatNumber(simulation.recommendedAtk)} ATK",
                                color = PrimaryViolet,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Оптимальный состав войск:",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            simulation.recommendedTroops.forEach { (unitKey, count) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = BattleAnalytics.getUnitDisplayName(unitKey),
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "~$count воинов",
                                        color = AccentGold,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            // Advice box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceDark)
                                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = simulation.recommendedCompositionAdvice,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            // TROOP RETRAINING TAB
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Восстановление недавних потерь",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ресурсы, необходимые для переобучения всех потерянных воинов",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            val totalFood = retrainCosts.sumOf { it.foodCost }
                            val totalWood = retrainCosts.sumOf { it.woodCost }
                            val totalStone = retrainCosts.sumOf { it.stoneCost }
                            val totalGold = retrainCosts.sumOf { it.goldCost }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ResourcePill(label = "Food", amount = totalFood, color = LootFood, icon = "🌾")
                                ResourcePill(label = "Wood", amount = totalWood, color = LootWood, icon = "🪵")
                                ResourcePill(label = "Stone", amount = totalStone, color = LootStone, icon = "🪨")
                                ResourcePill(label = "Gold", amount = totalGold, color = AccentGold, icon = "🪙")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Детализация по типам войск:",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(retrainCosts.size) { i ->
                    val item = retrainCosts[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.unitName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${item.countToTrain} шт.",
                                    color = LossRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "🌾 Еда: ${formatCompactNumber(item.foodCost)}", color = TextSecondary, fontSize = 12.sp)
                                Text(text = "🪵 Дерево: ${formatCompactNumber(item.woodCost)}", color = TextSecondary, fontSize = 12.sp)
                                Text(text = "🪨 Камень: ${formatCompactNumber(item.stoneCost)}", color = TextSecondary, fontSize = 12.sp)
                                Text(text = "🪙 Золото: ${formatCompactNumber(item.goldCost)}", color = AccentGold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    containerColor = SurfaceDark,
    labelColor = TextSecondary,
    selectedContainerColor = PrimaryViolet.copy(alpha = 0.25f),
    selectedLabelColor = PrimaryViolet
)
