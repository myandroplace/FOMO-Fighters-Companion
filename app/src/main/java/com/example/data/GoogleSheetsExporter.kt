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
     * Generates clean Tab-Separated Values (TSV) for all participants (Влияние и награды).
     */
    fun generateParticipantsTsv(sieges: List<OutpostSiege>): String {
        return buildString {
            append("Дата осады\tАванпост\tТип\tРанг\tИгрок\tКлан\tВлияние\tЕда 🌾\tДерево 🪵\tКамень 🪨\tАлмазы 💎\tЖетоны клана 🪙\n")
            for (s in sieges) {
                for (p in s.participants) {
                    append(s.date).append("\t")
                    append("${s.outpostLevel} ${s.outpostName}").append("\t")
                    append(s.outpostType).append("\t")
                    append(p.rank).append("\t")
                    append(p.playerName).append("\t")
                    append(p.clanTag).append("\t")
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

                        val partsArr = JSONArray()
                        for (p in s.participants) {
                            partsArr.put(JSONObject().apply {
                                put("name", p.playerName)
                                put("clan", p.clanTag)
                                put("influence", p.influenceFormatted)
                                put("rank", p.rank)
                                put("food", p.rewardFood)
                                put("wood", p.rewardWood)
                                put("stone", p.rewardStone)
                                put("gems", p.rewardGem)
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
     */
    fun getAppsScriptTemplate(): String {
        return """
function doPost(e) {
  var data = JSON.parse(e.postData.contents);
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
  
  if (sheet.getLastRow() === 0) {
    sheet.appendRow(["Дата", "Аванпост", "Тип", "Победитель", "Очки победителя", "Участников", "Топ игрок", "Влияние"]);
  }
  
  data.sieges.forEach(function(s) {
    var topPlayer = s.participants.length > 0 ? s.participants[0].name : "-";
    var topInf = s.participants.length > 0 ? s.participants[0].influence : "-";
    sheet.appendRow([s.date, s.outpost, s.type, s.winner, s.winnerScore, s.participants.length, topPlayer, topInf]);
  });
  
  return ContentService.createTextOutput(JSON.stringify({result: "success"})).setMimeType(ContentService.MimeType.JSON);
}
        """.trimIndent()
    }
}
