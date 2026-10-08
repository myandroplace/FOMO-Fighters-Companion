package com.example.data

import com.example.model.OutpostClan
import com.example.model.OutpostParticipant
import com.example.model.OutpostSiege
import org.json.JSONArray
import org.json.JSONObject

object OutpostData {

    val sampleSieges: List<OutpostSiege> = listOf(
        OutpostSiege(
            id = "siege_fort_20261007_211708",
            date = "07.10.2026, 21:17:08",
            timestamp = 1791398228000L,
            outpostLevel = 1,
            outpostName = "Форт",
            outpostType = "Логистика",
            clans = listOf(
                OutpostClan(
                    clanId = "clan_crypto_wolf",
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    score = 1_000_000_000L, // 1B
                    isWinner = true,
                    iconType = "runner"
                ),
                OutpostClan(
                    clanId = "clan_twilight_patrol",
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    score = 0L,
                    isWinner = false,
                    iconType = "crescent"
                )
            ),
            isCompleted = true,
            participants = listOf(
                OutpostParticipant("p1", "AlphaWolf_77", "Crypto Wolf", 19, 420_000_000L, 1, 150_000L, 120_000L, 80_000L, 500L, 1200L),
                OutpostParticipant("p2", "CyberFang", "Crypto Wolf", 19, 310_000_000L, 2, 110_000L, 95_000L, 65_000L, 350L, 950L),
                OutpostParticipant("p3", "MoonHunter", "Crypto Wolf", 19, 180_000_000L, 3, 75_000L, 60_000L, 45_000L, 200L, 600L),
                OutpostParticipant("p4", "IronPaw", "Crypto Wolf", 19, 90_000_000L, 4, 40_000L, 35_000L, 25_000L, 100L, 350L),
                OutpostParticipant("p5", "NightRunner", "Сумеречный патруль", 17, 0L, 5, 10_000L, 10_000L, 5_000L, 0L, 50L)
            )
        ),
        OutpostSiege(
            id = "siege_donjon_20261007_185247",
            date = "07.10.2026, 18:52:47",
            timestamp = 1791389567000L,
            outpostLevel = 1,
            outpostName = "Донжон",
            outpostType = "Боевые действия",
            clans = listOf(
                OutpostClan(
                    clanId = "clan_twilight_patrol",
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    score = 450_000_000L, // 450M
                    isWinner = true,
                    iconType = "crescent"
                ),
                OutpostClan(
                    clanId = "clan_swordart",
                    clanName = "SwordArt",
                    clanLevel = 12,
                    score = 411_000_000L, // 411M
                    isWinner = false,
                    iconType = "cross"
                )
            ),
            isCompleted = true,
            participants = listOf(
                OutpostParticipant("p6", "DuskVanguard", "Сумеречный патруль", 17, 210_000_000L, 1, 95_000L, 85_000L, 90_000L, 400L, 1000L),
                OutpostParticipant("p7", "Kirito_Slash", "SwordArt", 12, 195_000_000L, 2, 85_000L, 80_000L, 75_000L, 350L, 900L),
                OutpostParticipant("p8", "ShadowShield", "Сумеречный патруль", 17, 140_000_000L, 3, 65_000L, 55_000L, 60_000L, 250L, 700L),
                OutpostParticipant("p9", "Elucidator", "SwordArt", 12, 120_000_000L, 4, 50_000L, 45_000L, 50_000L, 200L, 550L),
                OutpostParticipant("p10", "MidnightWatcher", "Сумеречный патруль", 17, 100_000_000L, 5, 45_000L, 40_000L, 45_000L, 180L, 450L),
                OutpostParticipant("p11", "AsunaRapier", "SwordArt", 12, 96_000_000L, 6, 40_000L, 35_000L, 40_000L, 150L, 400L)
            )
        ),
        OutpostSiege(
            id = "siege_oplot_20261007_184024",
            date = "07.10.2026, 18:40:24",
            timestamp = 1791388824000L,
            outpostLevel = 1,
            outpostName = "Оплот",
            outpostType = "Экономический",
            clans = listOf(
                OutpostClan(
                    clanId = "clan_the_sparta",
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    score = 620_000_000L, // 620M
                    isWinner = true,
                    iconType = "helmet"
                ),
                OutpostClan(
                    clanId = "clan_twilight_patrol",
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    score = 85_000_000L, // 85M
                    isWinner = false,
                    iconType = "crescent"
                )
            ),
            isCompleted = true,
            participants = listOf(
                OutpostParticipant("p12", "Leonidas_300", "⚔️ The_Sparta ⚔️", 23, 280_000_000L, 1, 140_000L, 110_000L, 160_000L, 600L, 1500L),
                OutpostParticipant("p13", "HopliteCommander", "⚔️ The_Sparta ⚔️", 23, 190_000_000L, 2, 90_000L, 85_000L, 110_000L, 400L, 950L),
                OutpostParticipant("p14", "SpartanPhalanx", "⚔️ The_Sparta ⚔️", 23, 150_000_000L, 3, 70_000L, 65_000L, 85_000L, 300L, 750L),
                OutpostParticipant("p15", "EclipseRider", "Сумеречный патруль", 17, 85_000_000L, 4, 35_000L, 30_000L, 40_000L, 120L, 300L)
            )
        ),
        // 3-Clan Battle example (as user explicitly noted: "Может быть не 2, а больше кланов, кстати")
        OutpostSiege(
            id = "siege_citadel_20261007_143000",
            date = "07.10.2026, 14:30:00",
            timestamp = 1791373800000L,
            outpostLevel = 2,
            outpostName = "Цитадель",
            outpostType = "Боевые действия",
            clans = listOf(
                OutpostClan(
                    clanId = "clan_crypto_wolf",
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    score = 780_000_000L,
                    isWinner = true,
                    iconType = "runner"
                ),
                OutpostClan(
                    clanId = "clan_the_sparta",
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    score = 650_000_000L,
                    isWinner = false,
                    iconType = "helmet"
                ),
                OutpostClan(
                    clanId = "clan_swordart",
                    clanName = "SwordArt",
                    clanLevel = 12,
                    score = 310_000_000L,
                    isWinner = false,
                    iconType = "cross"
                )
            ),
            isCompleted = true,
            participants = listOf(
                OutpostParticipant("p16", "AlphaWolf_77", "Crypto Wolf", 19, 350_000_000L, 1, 160_000L, 140_000L, 120_000L, 700L, 1800L),
                OutpostParticipant("p17", "Leonidas_300", "⚔️ The_Sparta ⚔️", 23, 310_000_000L, 2, 140_000L, 125_000L, 110_000L, 600L, 1500L),
                OutpostParticipant("p18", "CyberFang", "Crypto Wolf", 19, 240_000_000L, 3, 110_000L, 95_000L, 85_000L, 450L, 1100L),
                OutpostParticipant("p19", "Kirito_Slash", "SwordArt", 12, 190_000_000L, 4, 85_000L, 75_000L, 70_000L, 350L, 850L),
                OutpostParticipant("p20", "HopliteCommander", "⚔️ The_Sparta ⚔️", 23, 180_000_000L, 5, 80_000L, 70_000L, 65_000L, 300L, 800L)
            )
        )
    )

    fun parseOutpostSiegesJson(rawJson: String): List<OutpostSiege> {
        val trimmed = rawJson.trim()
        val jsonArray: JSONArray = when {
            trimmed.startsWith("{") -> {
                val obj = JSONObject(trimmed)
                when {
                    obj.has("data") -> obj.getJSONArray("data")
                    obj.has("sieges") -> obj.getJSONArray("sieges")
                    obj.has("history") -> obj.getJSONArray("history")
                    else -> JSONArray()
                }
            }
            trimmed.startsWith("[") -> JSONArray(trimmed)
            else -> throw IllegalArgumentException("Неверный формат JSON")
        }

        val result = mutableListOf<OutpostSiege>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            result.add(parseSingleSiege(item))
        }
        return result
    }

    private fun parseSingleSiege(obj: JSONObject): OutpostSiege {
        val id = obj.optString("id", System.currentTimeMillis().toString())
        val date = obj.optString("date", "")
        val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
        val outpostLevel = obj.optInt("outpostLevel", 1)
        val outpostName = obj.optString("outpostName", "Форт")
        val outpostType = obj.optString("outpostType", "Логистика")
        val isCompleted = obj.optBoolean("isCompleted", true)

        val clansList = mutableListOf<OutpostClan>()
        val clansArr = obj.optJSONArray("clans")
        if (clansArr != null) {
            for (j in 0 until clansArr.length()) {
                val c = clansArr.getJSONObject(j)
                clansList.add(
                    OutpostClan(
                        clanId = c.optString("clanId", "clan_$j"),
                        clanName = c.optString("clanName", "Клан $j"),
                        clanLevel = c.optInt("clanLevel", 1),
                        score = c.optLong("score", 0L),
                        isWinner = c.optBoolean("isWinner", false),
                        iconType = c.optString("iconType", "default")
                    )
                )
            }
        }

        val participantsList = mutableListOf<OutpostParticipant>()
        val partArr = obj.optJSONArray("participants")
        if (partArr != null) {
            for (k in 0 until partArr.length()) {
                val p = partArr.getJSONObject(k)
                participantsList.add(
                    OutpostParticipant(
                        playerId = p.optString("playerId", "p_$k"),
                        playerName = p.optString("playerName", "Игрок $k"),
                        clanName = p.optString("clanName", ""),
                        clanLevel = p.optInt("clanLevel", 1),
                        influence = p.optLong("influence", 0L),
                        rank = p.optInt("rank", k + 1),
                        rewardFood = p.optLong("rewardFood", 0L),
                        rewardWood = p.optLong("rewardWood", 0L),
                        rewardStone = p.optLong("rewardStone", 0L),
                        rewardGem = p.optLong("rewardGem", 0L),
                        rewardClanTokens = p.optLong("rewardClanTokens", 0L)
                    )
                )
            }
        }

        return OutpostSiege(
            id = id,
            date = date,
            timestamp = timestamp,
            outpostLevel = outpostLevel,
            outpostName = outpostName,
            outpostType = outpostType,
            clans = clansList,
            isCompleted = isCompleted,
            participants = participantsList
        )
    }
}
