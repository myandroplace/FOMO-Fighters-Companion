package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.MainViewModel
import com.example.model.OutpostClan
import com.example.model.OutpostSiege
import com.example.ui.components.ResourcePill
import com.example.ui.components.formatCompactNumber
import com.example.ui.theme.AccentGold
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
fun OutpostsScreen(
    viewModel: MainViewModel,
    onNavigateToSheets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sieges by viewModel.filteredOutpostSieges.collectAsState()
    val typeFilter by viewModel.filterOutpostType.collectAsState()
    val selectedSiege by viewModel.selectedSiege.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title and Quick Sheets button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "История осад",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Аванпосты: Форты, Донжоны, Оплоты",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onNavigateToSheets,
                modifier = Modifier.testTag("outposts_to_sheets_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("В Google Таблицу", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Specialization Filters (Matching Game Badges)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                selected = typeFilter == null,
                onClick = { viewModel.setFilterOutpostType(null) },
                label = { Text("Все типы") },
                colors = outpostFilterChipColors()
            )
            FilterChip(
                selected = typeFilter == "Логистика",
                onClick = { viewModel.setFilterOutpostType(if (typeFilter == "Логистика") null else "Логистика") },
                label = { Text("📦 Логистика") },
                colors = outpostFilterChipColors()
            )
            FilterChip(
                selected = typeFilter == "Боевые действия",
                onClick = { viewModel.setFilterOutpostType(if (typeFilter == "Боевые действия") null else "Боевые действия") },
                label = { Text("⚔️ Боевые действия") },
                colors = outpostFilterChipColors()
            )
            FilterChip(
                selected = typeFilter == "Экономический",
                onClick = { viewModel.setFilterOutpostType(if (typeFilter == "Экономический") null else "Экономический") },
                label = { Text("💰 Экономический") },
                colors = outpostFilterChipColors()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Завершённые осады: ${sieges.size}",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sieges List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sieges, key = { it.id }) { siege ->
                OutpostSiegeCard(
                    siege = siege,
                    onClick = { viewModel.selectSiege(siege) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Detail Participants Dialog ("Влияние и награды участников")
    selectedSiege?.let { siege ->
        OutpostParticipantsDialog(
            siege = siege,
            onDismiss = { viewModel.selectSiege(null) }
        )
    }
}

@Composable
private fun OutpostSiegeCard(
    siege: OutpostSiege,
    onClick: () -> Unit
) {
    var clansExpanded by remember { mutableStateOf(false) }

    // Badge color according to outpost specialization
    val typeBadgeBg = when (siege.outpostType) {
        "Логистика" -> Color(0xFFD97706) // Amber/Gold brown
        "Боевые действия" -> Color(0xFF991B1B) // Crimson / Dark Red
        "Экономический" -> Color(0xFF065F46) // Emerald dark green
        else -> Color(0xFF374151)
    }

    val clan1 = siege.clans.getOrNull(0)
    val clan2 = siege.clans.getOrNull(1)

    // Calculate score ratio for the bar (red/orange vs blue)
    val score1 = clan1?.score ?: 0L
    val score2 = clan2?.score ?: 0L
    val totalScore = score1 + score2
    val fraction1 = if (totalScore > 0L) (score1.toFloat() / totalScore.toFloat()).coerceIn(0.05f, 0.95f) else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("outpost_siege_card_${siege.id}")
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Timestamp header
            Text(
                text = siege.date,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Outpost Name and Type Badge Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Outpost Level Badge (e.g. "1")
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentGold.copy(alpha = 0.2f))
                        .border(1.dp, AccentGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${siege.outpostLevel}",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = siege.outpostName,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Outpost Specialization Badge (Логистика / Боевые действия / Экономический)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = siege.outpostType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clans Row (Attacker vs Defender)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clan 1
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClanIcon(type = clan1?.iconType ?: "runner")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = clan1?.tag ?: "-",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Clan 2
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    ClanIcon(type = clan2?.iconType ?: "crescent")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = clan2?.tag ?: "-",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score Power Bar (Exact match to game screenshot: Red/Orange Attacker vs Blue Defender)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4338CA)) // Defender Blue base
            ) {
                // Attacker Bar (Red/Orange)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction1)
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFF97316), Color(0xFFEF4444))
                            )
                        )
                )

                // Scores & Trophy inside the bar
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Clan 1 score + Trophy if winner
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚔️ ${clan1?.scoreFormatted ?: "0"}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (clan1?.isWinner == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "🏆", fontSize = 14.sp)
                        }
                    }

                    // Clan 2 score + Trophy if winner
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (clan2?.isWinner == true) {
                            Text(text = "🏆", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "${clan2?.scoreFormatted ?: "0"} 🛡️",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable "Показать N участвующих кланов" row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { clansExpanded = !clansExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Показать ${siege.participatingClansCount} участвующих кланов",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (clansExpanded) Icons.Filled.KeyboardArrowUp else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded All Clans list (handles 2, 3, or more clans!)
            AnimatedVisibility(visible = clansExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    HorizontalDivider(color = SurfaceCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    siege.clans.forEachIndexed { idx, clan ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${idx + 1}",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ClanIcon(type = clan.iconType)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = clan.tag,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (clan.isWinner) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "🏆", fontSize = 12.sp)
                                }
                            }

                            Text(
                                text = clan.scoreFormatted,
                                color = if (clan.isWinner) AccentGold else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 Нажмите на карточку, чтобы открыть «Влияние и награды участников»",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OutpostParticipantsDialog(
    siege: OutpostSiege,
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
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Влияние и награды участников",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${siege.outpostLevel} ${siege.outpostName} (${siege.outpostType}) • ${siege.date}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Закрыть", tint = TextSecondary)
                        }
                    }
                    HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 8.dp))
                }

                if (siege.participants.isEmpty()) {
                    item {
                        Text(
                            text = "Нет данных об участниках этой осады",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    items(siege.participants, key = { it.playerId }) { p ->
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (p.rank) {
                                                        1 -> AccentGold.copy(alpha = 0.25f)
                                                        2 -> Color(0xFF94A3B8).copy(alpha = 0.25f)
                                                        3 -> Color(0xFFD97706).copy(alpha = 0.25f)
                                                        else -> Color.White.copy(alpha = 0.08f)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${p.rank}",
                                                color = when (p.rank) {
                                                    1 -> AccentGold
                                                    2 -> Color(0xFFCBD5E1)
                                                    3 -> Color(0xFFF59E0B)
                                                    else -> TextSecondary
                                                },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = p.playerName,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = p.clanTag,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = p.influenceFormatted,
                                            color = AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Влияние",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                if (p.totalRewardResources > 0 || p.rewardClanTokens > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        ResourcePill(label = "Food", amount = p.rewardFood, color = LootFood, icon = "🌾")
                                        ResourcePill(label = "Wood", amount = p.rewardWood, color = LootWood, icon = "🪵")
                                        ResourcePill(label = "Stone", amount = p.rewardStone, color = LootStone, icon = "🪨")
                                        if (p.rewardClanTokens > 0) {
                                            ResourcePill(label = "Tokens", amount = p.rewardClanTokens, color = PrimaryViolet, icon = "🪙")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClanIcon(type: String) {
    val (emoji, bg) = when (type.lowercase()) {
        "runner" -> "🐺" to Color(0xFF10B981) // Green
        "crescent" -> "🌙" to Color(0xFF3B82F6) // Blue
        "cross" -> "⚔️" to Color(0xFF6366F1) // Indigo
        "helmet" -> "🛡️" to Color(0xFFEF4444) // Red
        else -> "🏰" to Color(0xFF6B7280)
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(bg.copy(alpha = 0.2f))
            .border(1.dp, bg, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 12.sp)
    }
}

@Composable
private fun outpostFilterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = SurfaceCard,
    labelColor = TextSecondary,
    selectedContainerColor = PrimaryViolet.copy(alpha = 0.25f),
    selectedLabelColor = PrimaryViolet
)
