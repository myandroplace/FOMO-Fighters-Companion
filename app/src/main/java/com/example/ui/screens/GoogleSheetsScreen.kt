package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.AccentGold
import com.example.ui.theme.LootFood
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GoogleSheetsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetConfig by viewModel.sheetConfig.collectAsState()
    val sieges by viewModel.outpostSieges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val totalParticipants = viewModel.totalParticipantsCount

    var editableSheetUrl by remember(sheetConfig) { mutableStateOf(sheetConfig.sheetUrl) }
    var editableWebhookUrl by remember(sheetConfig) { mutableStateOf(sheetConfig.webhookUrl) }
    var previewMode by remember { mutableStateOf(0) } // 0: 3-колоночный формат (Дата, Аванпост, Вклад), 1: 4-колоночный, 2: Полный

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Экспорт в Google Таблицы",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Выгрузка детальной статистики влияния и наград со всех аванпостов",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Primary: Detailed Participants Export Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detailed_participants_export_card")
                    .border(1.dp, AccentGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Детализация участников аванпостов",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "$totalParticipants записей бойцов со всех ${sieges.size} осад",
                                    color = AccentGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Структура таблицы: Дата, Название аванпоста, Вклад (например: Аль Чаг 35М). При вставке в Google Sheets (Ctrl+V) данные автоматически распределяются по колонкам.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Button 1: Exact 3-column format as requested by user
                    Button(
                        onClick = {
                            val tsv = viewModel.getCompact3ColTsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Дата | Аванпост | Вклад", tsv))
                            Toast.makeText(
                                context,
                                "Скопировано $totalParticipants участников (Дата, Аванпост, Вклад)! Вставьте (Ctrl+V) в Google Sheets",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_compact_3col_tsv_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Скопировать: Дата | Аванпост | Вклад (Аль Чаг 35М)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button 2: 4-column structured format (Дата | Аванпост | Участник | Вклад)
                    Button(
                        onClick = {
                            val tsv = viewModel.getStandard4ColTsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Дата | Аванпост | Участник | Вклад", tsv))
                            Toast.makeText(
                                context,
                                "Скопировано $totalParticipants участников (4 колонки: Дата, Аванпост, Участник, Вклад)!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_standard_4col_tsv_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
                    ) {
                        Icon(Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Скопировать: Дата | Аванпост | Участник | Вклад",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button 3: Full Detailed with all rewards
                    OutlinedButton(
                        onClick = {
                            val tsv = viewModel.getParticipantsTsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Полная детализация участников всех аванпостов", tsv))
                            Toast.makeText(
                                context,
                                "Скопирована полная таблица участников со всеми ресурсами и наградами!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_participants_tsv_button")
                    ) {
                        Icon(Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Скопировать полную таблицу (с наградами и ресурсами)", color = TextPrimary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Button 4: Copy Sieges Summary
                    OutlinedButton(
                        onClick = {
                            val tsv = viewModel.getSiegesTsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Сводка осад аванпостов", tsv))
                            Toast.makeText(context, "Сводка осад скопирована!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_sieges_tsv_button")
                    ) {
                        Icon(Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryViolet)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Скопировать сводку осад (общие данные)", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Preview Table of Participants with Mode Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Предпросмотр структуры таблицы",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Для Google Sheets",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Format Switcher Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = previewMode == 0,
                            onClick = { previewMode = 0 },
                            label = { Text("3 колонки", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = previewMode == 1,
                            onClick = { previewMode = 1 },
                            label = { Text("4 колонки", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryViolet,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = previewMode == 2,
                            onClick = { previewMode = 2 },
                            label = { Text("Полная", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Scrollable table preview
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .background(SurfaceDark, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            val allParticipantsFlat = sieges.flatMap { s ->
                                s.participants.map { p -> s to p }
                            }.take(10) // Show first 10 for preview

                            when (previewMode) {
                                0 -> {
                                    // 3-Column: Дата | Название аванпоста | Вклад (например Аль Чаг 35М)
                                    Row(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                            .padding(vertical = 4.dp, horizontal = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Text(text = "Дата", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                        Text(text = "Название аванпоста", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(130.dp))
                                        Text(text = "Вклад", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(140.dp))
                                    }

                                    HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                                    allParticipantsFlat.forEach { (s, p) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = s.date.split(",").firstOrNull() ?: s.date, color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                            Text(text = "${s.outpostLevel} ${s.outpostName}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(130.dp))
                                            Text(text = "${p.playerName}     ${p.powerFormatted}", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(140.dp))
                                        }
                                    }
                                }
                                1 -> {
                                    // 4-Column: Дата | Название аванпоста | Участник | Вклад
                                    Row(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                            .padding(vertical = 4.dp, horizontal = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Text(text = "Дата", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                        Text(text = "Название аванпоста", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(130.dp))
                                        Text(text = "Участник", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(110.dp))
                                        Text(text = "Вклад", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(70.dp))
                                    }

                                    HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                                    allParticipantsFlat.forEach { (s, p) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = s.date.split(",").firstOrNull() ?: s.date, color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                            Text(text = "${s.outpostLevel} ${s.outpostName}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(130.dp))
                                            Text(text = p.playerName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                                            Text(text = p.powerFormatted, color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                                        }
                                    }
                                }
                                else -> {
                                    // Full view
                                    Row(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                                            .padding(vertical = 4.dp, horizontal = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(text = "Дата", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(75.dp))
                                        Text(text = "Аванпост", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(85.dp))
                                        Text(text = "Ур.", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(30.dp))
                                        Text(text = "Игрок", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                                        Text(text = "Вклад ⚔️", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(70.dp))
                                        Text(text = "Влияние 🪖", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(75.dp))
                                        Text(text = "Еда 🌾", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(65.dp))
                                    }

                                    HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                                    allParticipantsFlat.forEach { (s, p) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = s.date.split(",").firstOrNull() ?: "", color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(75.dp))
                                            Text(text = s.outpostName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(85.dp))
                                            Text(text = "${p.playerLevel}", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                                            Text(text = p.playerName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                                            Text(text = p.powerFormatted, color = TextPrimary, fontSize = 11.sp, modifier = Modifier.width(70.dp))
                                            Text(text = p.influenceFormatted, color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(75.dp))
                                            Text(text = formatRewardScore(p.rewardFood), color = LootFood, fontSize = 11.sp, modifier = Modifier.width(65.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Google Sheet Link & Webhook Integration Card
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Link, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Подключение к Google Таблице",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editableSheetUrl,
                        onValueChange = { editableSheetUrl = it },
                        label = { Text("Ссылка на Google Таблицу", color = TextSecondary) },
                        placeholder = { Text("https://docs.google.com/spreadsheets/d/...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = sheetsFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateSheetConfig(
                                    sheetConfig.copy(
                                        sheetUrl = editableSheetUrl.trim(),
                                        webhookUrl = editableWebhookUrl.trim()
                                    )
                                )
                                Toast.makeText(context, "Настройки сохранены!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_sheet_config_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet)
                        ) {
                            Text("Сохранить ссылку", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (editableSheetUrl.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(editableSheetUrl.trim()))
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Укажите ссылку на таблицу", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_google_sheet_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Открыть таблицу", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = SurfaceCardBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto-sync Webhook
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Webhook, contentDescription = null, tint = ProfitGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Отправка в Google Таблицу через Webhook",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Вставьте URL Google Apps Script для автоматической заливки всех строк осад и участников без ручного копирования.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editableWebhookUrl,
                        onValueChange = { editableWebhookUrl = it },
                        label = { Text("URL Google Apps Script Webhook", color = TextSecondary) },
                        placeholder = { Text("https://script.google.com/macros/s/.../exec", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = sheetsFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.syncToGoogleSheetsWebhook() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sync_to_webhook_button"),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Отправка в таблицу...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Отправить в Google Таблицу сейчас", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (statusMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statusMessage ?: "",
                            color = AccentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Apps Script Code Template
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Код скрипта Google Apps Script",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "В вашей таблице откройте Расширения -> Apps Script, вставьте этот код и опубликуйте как Веб-приложение:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            val script = viewModel.getAppsScriptTemplate()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Google Apps Script", script))
                            Toast.makeText(context, "Код скрипта скопирован в буфер обмена!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_apps_script_button")
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Скопировать код Apps Script", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun sheetsFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = SurfaceDark,
    unfocusedContainerColor = SurfaceDark,
    focusedIndicatorColor = PrimaryViolet,
    unfocusedIndicatorColor = SurfaceCardBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = PrimaryViolet
)

private fun formatRewardScore(amount: Long): String {
    return when {
        amount >= 1_000_000_000L -> String.format(java.util.Locale.US, "%.0fB", amount / 1_000_000_000.0)
        amount >= 1_000_000L -> String.format(java.util.Locale.US, "%.0fM", amount / 1_000_000.0)
        amount >= 1_000L -> String.format(java.util.Locale.US, "%.0fK", amount / 1_000.0)
        else -> amount.toString()
    }
}
