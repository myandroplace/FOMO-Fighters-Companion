package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.MainViewModel
import com.example.analytics.BattleAnalytics
import com.example.model.BattleLog
import com.example.ui.components.EfficiencyStars
import com.example.ui.components.ProfitLossBadge
import com.example.ui.components.ResourcePill
import com.example.ui.components.formatCompactNumber
import com.example.ui.components.formatNumber
import com.example.ui.theme.AccentGold
import com.example.ui.theme.LossRed
import com.example.ui.theme.LootFood
import com.example.ui.theme.LootGem
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BattlesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val filteredBattles by viewModel.filteredBattles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterRace by viewModel.filterRace.collectAsState()
    val filterEfficiency by viewModel.filterEfficiency.collectAsState()
    val selectedBattle by viewModel.selectedBattle.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("battle_search_field"),
            placeholder = { Text("Поиск по расе, дате, ID...", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Поиск",
                    tint = TextSecondary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Очистить",
                            tint = TextSecondary
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard,
                focusedIndicatorColor = PrimaryViolet,
                unfocusedIndicatorColor = SurfaceCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips (Race & Stars)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                selected = filterRace == null,
                onClick = { viewModel.setFilterRace(null) },
                label = { Text("Все расы") },
                colors = filterChipColors()
            )
            FilterChip(
                selected = filterRace == "skeleton",
                onClick = { viewModel.setFilterRace(if (filterRace == "skeleton") null else "skeleton") },
                label = { Text("💀 Скелеты") },
                colors = filterChipColors()
            )
            FilterChip(
                selected = filterRace == "goblin",
                onClick = { viewModel.setFilterRace(if (filterRace == "goblin") null else "goblin") },
                label = { Text("👺 Гоблины") },
                colors = filterChipColors()
            )
            FilterChip(
                selected = filterEfficiency == 3,
                onClick = { viewModel.setFilterEfficiency(if (filterEfficiency == 3) null else 3) },
                label = { Text("★★★") },
                colors = filterChipColors()
            )
            FilterChip(
                selected = filterEfficiency == 2,
                onClick = { viewModel.setFilterEfficiency(if (filterEfficiency == 2) null else 2) },
                label = { Text("★★") },
                colors = filterChipColors()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Найдено: ${filteredBattles.size} боев",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Battles List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredBattles, key = { it.id }) { battle ->
                BattleItemCard(
                    battle = battle,
                    onClick = { viewModel.selectBattle(battle) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Detail Dialog
    selectedBattle?.let { battle ->
        BattleDetailDialog(
            battle = battle,
            onDismiss = { viewModel.selectBattle(null) }
        )
    }
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = SurfaceCard,
    labelColor = TextSecondary,
    selectedContainerColor = PrimaryViolet.copy(alpha = 0.25f),
    selectedLabelColor = PrimaryViolet
)

@Composable
private fun BattleItemCard(
    battle: BattleLog,
    onClick: () -> Unit
) {
    val raceIcon = when (battle.targetRace.lowercase()) {
        "skeleton" -> "💀"
        "goblin" -> "👺"
        else -> "⚔️"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("battle_card_${battle.id}")
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = raceIcon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${battle.targetRace.replaceFirstChar { it.uppercase() }} Ур. ${battle.targetLevel}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    EfficiencyStars(efficiency = battle.attackerEfficiency)
                }

                Text(
                    text = battle.creationDate.takeLast(8),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Resource badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ResourcePill(label = "Food", amount = battle.attackerFood, color = LootFood, icon = "🌾")
                    ResourcePill(label = "Wood", amount = battle.attackerWood, color = LootWood, icon = "🪵")
                    ResourcePill(label = "Stone", amount = battle.attackerStone, color = LootStone, icon = "🪨")
                    ResourcePill(label = "Gem", amount = battle.attackerGem, color = LootGem, icon = "💎")
                }

                ProfitLossBadge(netProfit = battle.netProfit)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Losses & Exp Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (battle.attackerLooseTroopsCost > 0) {
                    Text(
                        text = "Потери: -${formatCompactNumber(battle.attackerLooseTroopsCost)}",
                        color = LossRed,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "Без потерь армии",
                        color = ProfitGreen,
                        fontSize = 11.sp
                    )
                }

                if (battle.leadExp > 0) {
                    Text(
                        text = "+${battle.leadExp} XP • ${battle.leadStarExp}⭐",
                        color = AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun BattleDetailDialog(
    battle: BattleLog,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Детали Боя",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = battle.creationDate,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Закрыть",
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SurfaceCardBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Outcome Overview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Цель: ${battle.targetRace.replaceFirstChar { it.uppercase() }} Ур. ${battle.targetLevel}", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "Защита лагеря: ${formatNumber(battle.targetDef)} DEF", color = TextSecondary, fontSize = 12.sp)
                        }
                        EfficiencyStars(efficiency = battle.attackerEfficiency)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Loot summary
                    Text(
                        text = "Добыча в этом бою:",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ResourcePill(label = "Food", amount = battle.attackerFood, color = LootFood, icon = "🌾")
                        ResourcePill(label = "Wood", amount = battle.attackerWood, color = LootWood, icon = "🪵")
                        ResourcePill(label = "Stone", amount = battle.attackerStone, color = LootStone, icon = "🪨")
                        ResourcePill(label = "Gem", amount = battle.attackerGem, color = LootGem, icon = "💎")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ваши войска (до / после):",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Attacker Troops List
                items(battle.attackerTroopsBefore.toList()) { (unit, before) ->
                    val after = battle.attackerTroopsAfter[unit] ?: 0
                    val lost = before - after
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = BattleAnalytics.getUnitDisplayName(unit),
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Row {
                            Text(text = "$before ➔ $after", color = TextSecondary, fontSize = 13.sp)
                            if (lost > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "(-$lost)", color = LossRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Войска лагеря врага:",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Target Troops List
                if (battle.targetTroopsBefore.isEmpty()) {
                    item {
                        Text(text = "Вражеский гарнизон уничтожен", color = ProfitGreen, fontSize = 12.sp)
                    }
                } else {
                    items(battle.targetTroopsBefore.toList()) { (unit, before) ->
                        val after = battle.targetTroopsAfter[unit] ?: 0
                        val lost = before - after
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = BattleAnalytics.getUnitDisplayName(unit),
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                            Row {
                                Text(text = "$before ➔ $after", color = TextSecondary, fontSize = 13.sp)
                                if (lost > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "(-$lost)", color = ProfitGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SurfaceCardBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "ID Цели (Лагеря):", color = TextMuted, fontSize = 11.sp)
                    Text(text = battle.targetId, color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ID Боя:", color = TextMuted, fontSize = 11.sp)
                    Text(text = battle.id, color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}
