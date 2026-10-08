package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.MainViewModel
import com.example.model.OutpostClan
import com.example.model.OutpostParticipant
import com.example.model.OutpostSiege
import com.example.ui.components.ResourcePill
import com.example.ui.theme.AccentGold
import com.example.ui.theme.LootFood
import com.example.ui.theme.LootStone
import com.example.ui.theme.LootWood
import com.example.ui.theme.LossRed
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
    val context = LocalContext.current
    val sieges by viewModel.filteredOutpostSieges.collectAsState()
    val allSieges by viewModel.outpostSieges.collectAsState()
    val selectedSiege by viewModel.selectedSiege.collectAsState()
    val typeFilter by viewModel.filterOutpostType.collectAsState()

    val totalParticipantsCount = viewModel.totalParticipantsCount

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "История осад",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Аванпосты: Твердыни, Форты, Донжоны, Оплоты",
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
                    Text("Таблицы", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // --- Master Participant Export Banner (Direct Answer to User Request!) ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("all_participants_export_card")
                    .border(1.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Детализация со ВСЕХ аванпостов",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$totalParticipantsCount участников • ${allSieges.size} завершённых осад",
                                    color = AccentGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Включает ник, уровень, вклад мощностью ⚔️, %, влияние 🪖 и награды (еда 🌾, дерево, камень) по каждому бойцу для Google Таблиц.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val tsv = viewModel.getCompact3ColTsv()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Дата | Название аванпоста | Вклад", tsv))
                                Toast.makeText(
                                    context,
                                    "Скопировано $totalParticipantsCount участников (Дата | Аванпост | Вклад)! Вставьте (Ctrl+V) в Google Таблицу",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_all_participants_tsv_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Скопировать вклад (Дата, Аванпост, Вклад)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToSheets,
                            modifier = Modifier.testTag("open_sheets_btn")
                        ) {
                            Icon(Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryViolet)
                        }
                    }
                }
            }
        }

        // Specialization Filters
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = typeFilter == null,
                    onClick = { viewModel.setFilterOutpostType(null) },
                    label = { Text("Все типы (${allSieges.size})") },
                    colors = outpostFilterChipColors()
                )
                FilterChip(
                    selected = typeFilter == "Экономический",
                    onClick = { viewModel.setFilterOutpostType(if (typeFilter == "Экономический") null else "Экономический") },
                    label = { Text("💰 Экономический") },
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
            }
        }

        // Sieges List
        items(sieges, key = { it.id }) { siege ->
            OutpostSiegeCard(
                siege = siege,
                onClick = { viewModel.selectSiege(siege) },
                onCopyParticipants = {
                    val tsv = viewModel.getSingleSiegeParticipantsTsv(siege)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Участники осады ${siege.outpostName}", tsv))
                    Toast.makeText(context, "Скопировано ${siege.totalParticipantsCount} участников этой осады!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Detail Participants Dialog ("Влияние и награды на участника" - matches Screenshot 2)
    selectedSiege?.let { siege ->
        OutpostParticipantsDialog(
            siege = siege,
            onDismiss = { viewModel.selectSiege(null) },
            onCopySingleSiege = {
                val tsv = viewModel.getSingleSiegeParticipantsTsv(siege)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Участники осады ${siege.outpostName}", tsv))
                Toast.makeText(context, "Скопировано ${siege.totalParticipantsCount} участников этой осады!", Toast.LENGTH_SHORT).show()
            },
            onCopyAllSieges = {
                val tsv = viewModel.getParticipantsTsv()
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Все участники аванпостов", tsv))
                Toast.makeText(context, "Скопировано $totalParticipantsCount участников со всех осад!", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
private fun OutpostSiegeCard(
    siege: OutpostSiege,
    onClick: () -> Unit,
    onCopyParticipants: () -> Unit
) {
    var clansExpanded by remember { mutableStateOf(false) }

    val typeBadgeBg = when (siege.outpostType) {
        "Логистика" -> Color(0xFFD97706)
        "Боевые действия" -> Color(0xFF991B1B)
        "Экономический" -> Color(0xFF065F46)
        else -> Color(0xFF374151)
    }

    val clan1 = siege.clans.getOrNull(0)
    val clan2 = siege.clans.getOrNull(1)

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

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = siege.outpostType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2 Main Clans Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clan 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    ClanIcon(type = clan1?.iconType ?: "default")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = clan1?.tag ?: "-",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Clan 2
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f)
                ) {
                    ClanIcon(type = clan2?.iconType ?: "default")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = clan2?.tag ?: "-",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score Battle Bar (Red/Orange vs Blue with Trophies/Shields)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                // Side 1 (Attacker / Clan 1)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(fraction1)
                        .background(Color(0xFFEA580C))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚔️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = clan1?.scoreFormatted ?: "0",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (clan1?.isWinner == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "🏆", fontSize = 11.sp)
                        }
                    }
                }

                // Side 2 (Defender / Clan 2)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f - fraction1)
                        .background(Color(0xFF3B82F6))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = clan2?.scoreFormatted ?: "0",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (clan2?.isWinner == true) "🏆" else "🛡️",
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Clans Toggle (Handles 2, 3, or more clans)
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

            // Expanded All Clans list
            AnimatedVisibility(visible = clansExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    siege.clans.forEach { clan ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SurfaceCardBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Detail click hint row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Влияние и награды: ${siege.totalParticipantsCount} бойцов",
                        color = AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryViolet.copy(alpha = 0.2f))
                        .clickable { onCopyParticipants() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = PrimaryViolet, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Копировать", color = PrimaryViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog matching Screenshot 2: "Влияние и награды на участника"
 */
@Composable
fun OutpostParticipantsDialog(
    siege: OutpostSiege,
    onDismiss: () -> Unit,
    onCopySingleSiege: () -> Unit,
    onCopyAllSieges: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B29)),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Title & Close Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Влияние и награды на участника",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${siege.outpostLevel} ${siege.outpostName} • ${siege.outpostType} • ${siege.date}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Закрыть", tint = TextSecondary)
                        }
                    }
                    HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 6.dp))
                }

                // Section: "Ваши награды:" (as seen in Screenshot 2)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Ваши награды:",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Resource numbers row: 🪖 16 053 688  🌾 933K  🪵 933K  🪨 725K
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪖", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatRewardNumber(siege.myRewardInfluence),
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🌾", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "933K", color = LootFood, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪵", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "933K", color = LootWood, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪨", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "725K", color = LootStone, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Получено", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))

                            // Badges: EXP (+36K), Star (+413), Chest (+6)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RewardBadge(label = "EXP", value = "+36K", bgColor = Color(0xFF374151), textColor = Color.White)
                                RewardBadge(label = "★", value = "+413", bgColor = Color(0xFF2563EB), textColor = Color.White)
                                RewardBadge(label = "📦", value = "+6", bgColor = Color(0xFF059669), textColor = Color.White)
                            }
                        }
                    }
                }

                // Section: "Всего ⚔️ 450 710 761 (83%) / 542 403 000" (as seen in Screenshot 2)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Всего ⚔️ ",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "450 710 761 (${siege.powerPercentSent}) ",
                                    color = AccentGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "/ 542 403 000",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "клан отправил мощность / доступная мощь",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Copy Action Buttons Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCopySingleSiege,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_this_siege_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Эта осада (TSV)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onCopyAllSieges,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_all_sieges_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                        ) {
                            Icon(Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ВСЕ осады (TSV)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Participants Table Column Headers: "Участник и награды" | "Вклад"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Участник и награды",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Вклад",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Participant List Items (matches Screenshot 2!)
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
                        ParticipantItemCard(p = p)
                    }
                }
            }
        }
    }
}

/**
 * Individual participant card row matching Screenshot 2:
 * [Level badge] PlayerName + Avatar
 * Under: 🪖 Influence + 🌾 Food
 * Right: ⚔️ Power (Percent)
 */
@Composable
private fun ParticipantItemCard(p: OutpostParticipant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: [Level badge] Nickname + Avatar, then influence & food below
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Level Badge (e.g. [33], [25], [34])
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFD97706).copy(alpha = 0.25f))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${p.playerLevel}",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = p.playerName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Player avatar badge
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF60A5FA).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF60A5FA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛡️", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Stats under player name: 🪖 294M  🌾 47M
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪖", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = p.influenceFormatted,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                        if (p.rewardFood > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌾", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = formatPowerOrScore(p.rewardFood),
                                    color = LootFood,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Right Side: "Вклад": ⚔️ 34M (100%)
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚔️", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = p.powerFormatted,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = p.contributionPercent,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun RewardBadge(
    label: String,
    value: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ClanIcon(type: String) {
    val (emoji, bg) = when (type.lowercase()) {
        "runner" -> "🐺" to Color(0xFF10B981)
        "crescent" -> "🌙" to Color(0xFF3B82F6)
        "cross" -> "⚔️" to Color(0xFF6366F1)
        "helmet" -> "🛡️" to Color(0xFFEF4444)
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

private fun formatRewardNumber(amount: Long): String {
    return String.format(java.util.Locale.US, "%,d", amount).replace(',', ' ')
}

private fun formatPowerOrScore(score: Long): String {
    return when {
        score >= 1_000_000_000L -> String.format(java.util.Locale.US, "%.0fB", score / 1_000_000_000.0)
        score >= 1_000_000L -> String.format(java.util.Locale.US, "%.0fM", score / 1_000_000.0)
        score >= 1_000L -> String.format(java.util.Locale.US, "%.0fK", score / 1_000.0)
        else -> score.toString()
    }
}

@Composable
private fun outpostFilterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = SurfaceCard,
    labelColor = TextSecondary,
    selectedContainerColor = PrimaryViolet.copy(alpha = 0.25f),
    selectedLabelColor = PrimaryViolet
)
