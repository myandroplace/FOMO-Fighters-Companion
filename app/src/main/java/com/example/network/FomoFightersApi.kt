package com.example.network

import com.example.model.ApiConfig
import com.example.model.BattleLog
import com.example.model.DropItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object FomoFightersApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun fetchBattleLogs(config: ApiConfig): Result<List<BattleLog>> = withContext(Dispatchers.IO) {
        try {
            val jsonMedia = "application/json; charset=utf-8".toMediaType()
            val body = "{}".toRequestBody(jsonMedia)

            val requestBuilder = Request.Builder()
                .url(config.apiUrl)
                .post(body)
                .header("accept", "*/*")
                .header("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("api-key", config.apiKey.trim())
                .header("api-hash", config.apiHash.trim())
                .header("api-time", config.apiTime.trim())
                .header("api-version", config.apiVersion.trim())
                .header("content-type", "application/json")
                .header("origin", config.origin.trim())
                .header("referer", config.referer.trim())
                .header("user-agent", config.userAgent.trim())

            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("HTTP ${response.code}: ${response.message}\n$responseBody")
                )
            }

            val logs = parseBattleLogsJson(responseBody)
            Result.success(logs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parseBattleLogsJson(rawJson: String): List<BattleLog> {
        val trimmed = rawJson.trim()
        val jsonArray: JSONArray = when {
            trimmed.startsWith("{") -> {
                val obj = JSONObject(trimmed)
                if (obj.has("data")) {
                    obj.getJSONArray("data")
                } else {
                    JSONArray()
                }
            }
            trimmed.startsWith("[") -> {
                JSONArray(trimmed)
            }
            else -> throw IllegalArgumentException("Неверный формат JSON")
        }

        val list = mutableListOf<BattleLog>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            list.add(parseSingleBattleLog(item))
        }
        return list
    }

    private fun parseSingleBattleLog(obj: JSONObject): BattleLog {
        return BattleLog(
            id = obj.optString("id", ""),
            attackerId = obj.optString("attackerId", ""),
            targetId = obj.optString("targetId", ""),
            timerId = if (obj.has("timerId") && !obj.isNull("timerId")) obj.getString("timerId") else null,
            battleType = obj.optString("battleType", "attack"),
            attackerName = obj.optString("attackerName", ""),
            attackerRace = obj.optString("attackerRace", ""),
            attackerLevel = obj.optInt("attackerLevel", 1),
            attackerPower = obj.optLong("attackerPower", 0),
            attackerAtk = obj.optLong("attackerAtk", 0),
            attackerTroopsBefore = parseTroopMap(obj.opt("attackerTroopsBefore")),
            attackerTroopsAfter = parseTroopMap(obj.opt("attackerTroopsAfter")),
            attackerExp = obj.optLong("attackerExp", 0),
            attackerLooseAtk = obj.optLong("attackerLooseAtk", 0),
            attackerLooseDef = obj.optLong("attackerLooseDef", 0),
            attackerLooseTroopsCost = obj.optLong("attackerLooseTroopsCost", 0),
            attackerEfficiency = obj.optInt("attackerEfficiency", 1),
            attackerFood = obj.optLong("attackerFood", 0),
            attackerWood = obj.optLong("attackerWood", 0),
            attackerStone = obj.optLong("attackerStone", 0),
            attackerGem = obj.optLong("attackerGem", 0),
            targetType = obj.optString("targetType", "camp"),
            targetName = obj.optString("targetName", ""),
            targetRace = obj.optString("targetRace", ""),
            targetLevel = obj.optInt("targetLevel", 0),
            targetPower = obj.optLong("targetPower", 0),
            targetLooseAtk = obj.optLong("targetLooseAtk", 0),
            targetLooseDef = obj.optLong("targetLooseDef", 0),
            targetLooseTroopsCost = obj.optLong("targetLooseTroopsCost", 0),
            targetDef = obj.optLong("targetDef", 0),
            targetExp = obj.optLong("targetExp", 0),
            targetTroopsBefore = parseTroopMap(obj.opt("targetTroopsBefore")),
            targetTroopsAfter = parseTroopMap(obj.opt("targetTroopsAfter")),
            creationDate = obj.optString("creationDate", ""),
            isScoutSuccess = obj.optBoolean("isScoutSuccess", false),
            isScout = obj.optBoolean("isScout", false),
            attackerItems = parseDropItems(obj.opt("attackerItems")),
            attackerAvatar = if (obj.has("attackerAvatar") && !obj.isNull("attackerAvatar")) obj.getString("attackerAvatar") else null,
            isRead = obj.optBoolean("isRead", false)
        )
    }

    private fun parseTroopMap(obj: Any?): Map<String, Int> {
        if (obj == null || obj !is JSONObject) return emptyMap()
        val map = mutableMapOf<String, Int>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = obj.optInt(key, 0)
        }
        return map
    }

    private fun parseDropItems(obj: Any?): List<DropItem> {
        if (obj == null || obj !is JSONArray) return emptyList()
        val list = mutableListOf<DropItem>()
        for (i in 0 until obj.length()) {
            val item = obj.getJSONObject(i)
            val type = item.optString("type", "")
            val count = item.optInt("count", 0)
            val data = if (item.has("data") && !item.isNull("data")) item.optInt("data") else null
            list.add(DropItem(type, count, data))
        }
        return list
    }

    fun parseCurlCommand(curlText: String, currentConfig: ApiConfig): ApiConfig {
        var url = currentConfig.apiUrl
        var key = currentConfig.apiKey
        var hash = currentConfig.apiHash
        var time = currentConfig.apiTime
        var version = currentConfig.apiVersion
        var origin = currentConfig.origin
        var referer = currentConfig.referer
        var userAgent = currentConfig.userAgent

        // Extract URL
        val urlMatcher = Pattern.compile("curl\\s+(?:--url\\s+)?['\"]?([^'\"\\s]+)['\"]?").matcher(curlText)
        if (urlMatcher.find()) {
            val foundUrl = urlMatcher.group(1)
            if (foundUrl != null && foundUrl.startsWith("http")) {
                url = foundUrl
            }
        }

        // Extract headers: -H 'key: value' or -H "key: value"
        val headerMatcher = Pattern.compile("-H\\s+['\"]([^:'\"]+):\\s*([^'\"]*)['\"]").matcher(curlText)
        while (headerMatcher.find()) {
            val hName = headerMatcher.group(1)?.lowercase()?.trim() ?: ""
            val hVal = headerMatcher.group(2)?.trim() ?: ""
            when (hName) {
                "api-key" -> key = hVal
                "api-hash" -> hash = hVal
                "api-time" -> time = hVal
                "api-version" -> version = hVal
                "origin" -> origin = hVal
                "referer" -> referer = hVal
                "user-agent" -> userAgent = hVal
            }
        }

        return currentConfig.copy(
            apiUrl = url,
            apiKey = key,
            apiHash = hash,
            apiTime = time,
            apiVersion = version,
            origin = origin,
            referer = referer,
            userAgent = userAgent
        )
    }
}
