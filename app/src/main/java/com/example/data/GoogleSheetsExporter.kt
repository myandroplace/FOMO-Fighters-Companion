package com.example.data

import com.example.model.OutpostSiege
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GoogleSheetsExporter {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Generates clean Tab-Separated Values (TSV) for the main sieges table.
     * Pasting TSV directly into Google Sheets automatically fills separate columns and rows!
     */
    fun generateSiegesTsv(sieges: List<OutpostSiege>): String {
        return buildString {
            // Headers
            append("Дата и время\tУр.\tАванпост\tТип\tПобедитель\tОчки победителя\tСоперник (Атака/Оборона)\tОчки соперника\tВсего кланов\tУчастников\tТоп игрок\tВлияние Топ игрока\n")
            for (s in sieges) {
                val winner = s.winnerClan
                val secondClan = s.clans.firstOrNull { it != winner } ?: s.clans.getOrNull(1)
                val topParticipant = s.participants.maxByOrNull { it.influence }

                append(s.date).append("\t")
                append(s.outpostLevel).append("\t")
                append(s.outpostName).append("\t")
                append(s.outpostType).append("\t")
                append(winner?.tag ?: "-").append("\t")
                append(winner?.scoreFormatted ?: "0").append("\t")
                append(secondClan?.tag ?: "-").append("\t")
                append(secondClan?.scoreFormatted ?: "0").append("\t")
                append(s.participatingClansCount).append("\t")
                append(s.totalParticipantsCount).append("\t")
                append(topParticipant?.playerName ?: "-").append("\t")
                append(topParticipant?.influenceFormatted ?: "-").append("\n")
            }
        }
    }

    /**
     * Exact format requested by user:
     * 3 columns: "Дата", "Название аванпоста", "Вклад"
     * Where "Вклад" column contains: "Аль Чаг 35М" (Player name and contribution)
     */
    fun generateCompact3ColTsv(sieges: List<OutpostSiege>): String {
        return buildString {
            append("Дата\tНазвание аванпоста\tВклад\n")
            for (s in sieges) {
                val outpostFull = "${s.outpostLevel} ${s.outpostName}"
                for (p in s.participants) {
                    append(s.date).append("\t")
                    append(outpostFull).append("\t")
                    append("${p.playerName} ${p.powerFormatted}").append("\n")
                }
            }
        }
    }

    /**
     * Alternative structured format:
     * 4 columns: "Дата", "Название аванпоста", "Участник", "Вклад"
     * Ready for sorting, filtering, and pivot tables in Google Sheets!
     */
    fun generateStandard4ColTsv(sieges: List<OutpostSiege>): String {
        return buildString {
            append("Дата\tНазвание аванпоста\tУчастник\tВклад\n")
            for (s in sieges) {
                val outpostFull = "${s.outpostLevel} ${s.outpostName}"
                for (p in s.participants) {
                    append(s.date).append("\t")
                    append(outpostFull).append("\t")
                    append(p.playerName).append("\t")
                    append(p.powerFormatted).append("\n")
                }
            }
        }
    }

    /**
     * Generates clean Tab-Separated Values (TSV) for all participants (Влияние и награды) across all sieges.
     * Ready for direct Ctrl+V pasting into Google Sheets!
     */
    fun generateParticipantsTsv(sieges: List<OutpostSiege>): String {
        return buildString {
            append("Дата осады\tВремя\tАванпост\tСпециализация\tРанг\tУр. игрока\tИгрок\tКлан\tВклад (мощь ⚔️)\tВклад (%)\tВлияние (🪖)\tНаграда Еда (🌾)\tНаграда Дерево (🪵)\tНаграда Камень (🪨)\tАлмазы (💎)\tТокены клана (🪙)\n")
            for (s in sieges) {
                val dateParts = s.date.split(", ")
                val dateOnly = dateParts.getOrElse(0) { s.date }
                val timeOnly = dateParts.getOrElse(1) { "" }

                for (p in s.participants) {
                    append(dateOnly).append("\t")
                    append(timeOnly).append("\t")
                    append("${s.outpostLevel} ${s.outpostName}").append("\t")
                    append(s.outpostType).append("\t")
                    append(p.rank).append("\t")
                    append(p.playerLevel).append("\t")
                    append(p.playerName).append("\t")
                    append(p.clanTag).append("\t")
                    append(p.powerFormatted).append("\t")
                    append(p.contributionPercent).append("\t")
                    append(p.influenceFormatted).append("\t")
                    append(p.rewardFood).append("\t")
                    append(p.rewardWood).append("\t")
                    append(p.rewardStone).append("\t")
                    append(p.rewardGem).append("\t")
                    append(p.rewardClanTokens).append("\n")
                }
            }
        }
    }

    /**
     * Generates TSV for participants of a SINGLE siege.
     */
    fun generateSingleSiegeParticipantsTsv(siege: OutpostSiege): String {
        return generateParticipantsTsv(listOf(siege))
    }

    /**
     * POST siege data to a user-configured Google Apps Script Webhook.
     */
    suspend fun sendToWebhook(webhookUrl: String, sieges: List<OutpostSiege>): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (webhookUrl.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("URL вебхука Google Таблицы не указан"))
            }

            val payload = JSONObject().apply {
                put("action", "sync_outposts")
                put("timestamp", System.currentTimeMillis())

                val siegesArr = JSONArray()
                for (s in sieges) {
                    val sObj = JSONObject().apply {
                        put("id", s.id)
                        put("date", s.date)
                        put("outpost", "${s.outpostLevel} ${s.outpostName}")
                        put("type", s.outpostType)
                        put("winner", s.winnerClan?.tag ?: "")
                        put("winnerScore", s.winnerClan?.scoreFormatted ?: "0")
                        put("clansCount", s.participatingClansCount)
                        put("totalPowerSent", s.totalPowerSent)
                        put("powerPercentSent", s.powerPercentSent)

                        val partsArr = JSONArray()
                        for (p in s.participants) {
                            partsArr.put(JSONObject().apply {
                                put("name", p.playerName)
                                put("level", p.playerLevel)
                                put("clan", p.clanTag)
                                put("influence", p.influenceFormatted)
                                put("influenceRaw", p.influence)
                                put("power", p.powerFormatted)
                                put("powerPercent", p.contributionPercent)
                                put("rank", p.rank)
                                put("food", p.rewardFood)
                                put("wood", p.rewardWood)
                                put("stone", p.rewardStone)
                                put("gems", p.rewardGem)
                                put("tokens", p.rewardClanTokens)
                            })
                        }
                        put("participants", partsArr)
                    }
                    siegesArr.put(sObj)
                }
                put("sieges", siegesArr)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = payload.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(webhookUrl.trim())
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Result.failure(Exception("HTTP ${response.code}: $respBody"))
            } else {
                Result.success("Данные успешно отправлены в Google Таблицу (${sieges.size} осад)")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ready-to-copy Google Apps Script code for the user to paste into their Google Sheet.
     * Automatically creates and updates both 'Участники' (с колонками Дата, Аванпост, Участник, Вклад)
     * and 'Сводка осад'!
     */
    fun getAppsScriptTemplate(): String {
        return """
function doPost(e) {
  var data = JSON.parse(e.postData.contents);
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  
  // 1. Лист 'Участники' (структура: Дата | Название аванпоста | Участник | Вклад | Влияние)
  var pSheet = ss.getSheetByName("Участники") || ss.insertSheet("Участники");
  if (pSheet.getLastRow() === 0) {
    pSheet.appendRow(["Дата", "Название аванпоста", "Участник", "Вклад", "Влияние", "Клан"]);
    pSheet.getRange(1, 1, 1, 6).setFontWeight("bold").setBackground("#f3f4f6");
  }
  
  // 2. Лист 'Сводка осад'
  var sSheet = ss.getSheetByName("Сводка осад") || ss.insertSheet("Сводка осад");
  if (sSheet.getLastRow() === 0) {
    sSheet.appendRow(["Дата", "Аванпост", "Тип", "Победитель", "Очки победителя", "Всего кланов", "Участников"]);
    sSheet.getRange(1, 1, 1, 7).setFontWeight("bold").setBackground("#f3f4f6");
  }
  
  data.sieges.forEach(function(s) {
    // Добавляем сводку
    sSheet.appendRow([s.date, s.outpost, s.type, s.winner, s.winnerScore, s.clansCount, s.participants.length]);
    
    // Добавляем всех участников данного аванпоста
    s.participants.forEach(function(p) {
      pSheet.appendRow([s.date, s.outpost, p.name, p.power, p.influence, p.clan]);
    });
  });
  
  return ContentService.createTextOutput(JSON.stringify({result: "success"})).setMimeType(ContentService.MimeType.JSON);
}
        """.trimIndent()
    }
}
