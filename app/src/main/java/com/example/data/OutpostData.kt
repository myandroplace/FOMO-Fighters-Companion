package com.example.data

import com.example.model.OutpostClan
import com.example.model.OutpostParticipant
import com.example.model.OutpostSiege
import org.json.JSONArray
import org.json.JSONObject

object OutpostData {

    val sampleSieges: List<OutpostSiege> = listOf(
        // Осада 1: Твердыня (с реальными участниками из скриншота)
        OutpostSiege(
            id = "siege_tverdinya_20261008_204230",
            date = "08.10.2026, 20:42:30",
            timestamp = 1791482550000L,
            outpostLevel = 1,
            outpostName = "Твердыня",
            outpostType = "Экономический",
            totalPowerSent = 450_710_761L,
            totalPowerAvailable = 542_403_000L,
            powerPercentSent = "83%",
            myRewardInfluence = 16_053_688L,
            myRewardFood = 933_000L,
            myRewardWood = 933_000L,
            myRewardStone = 725_000L,
            myRewardExp = 36_000L,
            myRewardStars = 413L,
            myRewardChests = 6L,
            clans = listOf(
                OutpostClan(
                    clanId = "clan_twilight_patrol",
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    score = 422_000_000L, // 422M
                    isWinner = true,
                    iconType = "crescent"
                ),
                OutpostClan(
                    clanId = "clan_crypto_wolf",
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    score = 28_000_000L, // 28M
                    isWinner = false,
                    iconType = "runner"
                )
            ),
            isCompleted = true,
            participants = listOf(
                OutpostParticipant(
                    playerId = "p_alchag",
                    playerName = "Аль Чаг",
                    playerLevel = 33,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 294_000_000L,
                    rank = 1,
                    powerContribution = 34_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 47_000_000L,
                    rewardWood = 45_000_000L,
                    rewardStone = 38_000_000L,
                    rewardClanTokens = 1250L
                ),
                OutpostParticipant(
                    playerId = "p_bely",
                    playerName = "БΞЛЫЙ",
                    playerLevel = 25,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 213_000_000L,
                    rank = 2,
                    powerContribution = 25_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 34_000_000L,
                    rewardWood = 32_000_000L,
                    rewardStone = 28_000_000L,
                    rewardClanTokens = 980L
                ),
                OutpostParticipant(
                    playerId = "p_jabba",
                    playerName = "Battle Jabba",
                    playerLevel = 34,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 208_000_000L,
                    rank = 3,
                    powerContribution = 24_000_000L,
                    contributionPercent = "(94%)",
                    rewardFood = 42_000_000L,
                    rewardWood = 40_000_000L,
                    rewardStone = 35_000_000L,
                    rewardClanTokens = 950L
                ),
                OutpostParticipant(
                    playerId = "p_elina",
                    playerName = "Элина",
                    playerLevel = 27,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 195_000_000L,
                    rank = 4,
                    powerContribution = 22_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 31_000_000L,
                    rewardWood = 29_000_000L,
                    rewardStone = 25_000_000L,
                    rewardClanTokens = 880L
                ),
                OutpostParticipant(
                    playerId = "p_ragnar",
                    playerName = "Рагнар",
                    playerLevel = 31,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 168_000_000L,
                    rank = 5,
                    powerContribution = 19_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 27_000_000L,
                    rewardWood = 25_000_000L,
                    rewardStone = 22_000_000L,
                    rewardClanTokens = 750L
                ),
                OutpostParticipant(
                    playerId = "p_morpheus",
                    playerName = "Морфей",
                    playerLevel = 29,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 135_000_000L,
                    rank = 6,
                    powerContribution = 15_000_000L,
                    contributionPercent = "(98%)",
                    rewardFood = 22_000_000L,
                    rewardWood = 20_000_000L,
                    rewardStone = 18_000_000L,
                    rewardClanTokens = 620L
                ),
                OutpostParticipant(
                    playerId = "p_shewolf",
                    playerName = "Волчица",
                    playerLevel = 26,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 110_000_000L,
                    rank = 7,
                    powerContribution = 12_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 18_000_000L,
                    rewardWood = 16_000_000L,
                    rewardStone = 14_000_000L,
                    rewardClanTokens = 510L
                ),
                OutpostParticipant(
                    playerId = "p_skald",
                    playerName = "Скальд",
                    playerLevel = 30,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 95_000_000L,
                    rank = 8,
                    powerContribution = 10_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 15_000_000L,
                    rewardWood = 14_000_000L,
                    rewardStone = 12_000_000L,
                    rewardClanTokens = 440L
                ),
                OutpostParticipant(
                    playerId = "p_wolf1",
                    playerName = "WolfLeader",
                    playerLevel = 28,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 28_000_000L,
                    rank = 9,
                    powerContribution = 28_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 5_000_000L,
                    rewardWood = 4_000_000L,
                    rewardStone = 3_000_000L,
                    rewardClanTokens = 150L
                )
            )
        ),

        // Осада 2: Форт
        OutpostSiege(
            id = "siege_fort_20261007_211708",
            date = "07.10.2026, 21:17:08",
            timestamp = 1791398228000L,
            outpostLevel = 1,
            outpostName = "Форт",
            outpostType = "Логистика",
            totalPowerSent = 1_000_000_000L,
            totalPowerAvailable = 1_050_000_000L,
            powerPercentSent = "95%",
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
                OutpostParticipant(
                    playerId = "p_awolf",
                    playerName = "AlphaWolf_77",
                    playerLevel = 32,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 420_000_000L,
                    rank = 1,
                    powerContribution = 45_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 65_000_000L,
                    rewardWood = 55_000_000L,
                    rewardStone = 40_000_000L,
                    rewardClanTokens = 1500L
                ),
                OutpostParticipant(
                    playerId = "p_cfang",
                    playerName = "CyberFang",
                    playerLevel = 30,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 310_000_000L,
                    rank = 2,
                    powerContribution = 33_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 48_000_000L,
                    rewardWood = 42_000_000L,
                    rewardStone = 30_000_000L,
                    rewardClanTokens = 1200L
                ),
                OutpostParticipant(
                    playerId = "p_mhunt",
                    playerName = "MoonHunter",
                    playerLevel = 28,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 180_000_000L,
                    rank = 3,
                    powerContribution = 20_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 28_000_000L,
                    rewardWood = 24_000_000L,
                    rewardStone = 18_000_000L,
                    rewardClanTokens = 800L
                ),
                OutpostParticipant(
                    playerId = "p_ipaw",
                    playerName = "IronPaw",
                    playerLevel = 26,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 90_000_000L,
                    rank = 4,
                    powerContribution = 11_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 15_000_000L,
                    rewardWood = 12_000_000L,
                    rewardStone = 9_000_000L,
                    rewardClanTokens = 450L
                ),
                OutpostParticipant(
                    playerId = "p_nrunner",
                    playerName = "NightRunner",
                    playerLevel = 24,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 0L,
                    rank = 5,
                    powerContribution = 0L,
                    contributionPercent = "(0%)",
                    rewardFood = 0L,
                    rewardWood = 0L,
                    rewardStone = 0L,
                    rewardClanTokens = 50L
                )
            )
        ),

        // Осада 3: Донжон
        OutpostSiege(
            id = "siege_donjon_20261007_185247",
            date = "07.10.2026, 18:52:47",
            timestamp = 1791389567000L,
            outpostLevel = 1,
            outpostName = "Донжон",
            outpostType = "Боевые действия",
            totalPowerSent = 861_000_000L,
            totalPowerAvailable = 920_000_000L,
            powerPercentSent = "93%",
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
                OutpostParticipant(
                    playerId = "p_alchag2",
                    playerName = "Аль Чаг",
                    playerLevel = 33,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 210_000_000L,
                    rank = 1,
                    powerContribution = 26_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 35_000_000L,
                    rewardWood = 32_000_000L,
                    rewardStone = 28_000_000L,
                    rewardClanTokens = 1000L
                ),
                OutpostParticipant(
                    playerId = "p_kirito",
                    playerName = "Kirito_Slash",
                    playerLevel = 28,
                    clanName = "SwordArt",
                    clanLevel = 12,
                    influence = 195_000_000L,
                    rank = 2,
                    powerContribution = 24_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 32_000_000L,
                    rewardWood = 30_000_000L,
                    rewardStone = 25_000_000L,
                    rewardClanTokens = 900L
                ),
                OutpostParticipant(
                    playerId = "p_bely2",
                    playerName = "БΞЛЫЙ",
                    playerLevel = 25,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 140_000_000L,
                    rank = 3,
                    powerContribution = 18_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 23_000_000L,
                    rewardWood = 21_000_000L,
                    rewardStone = 19_000_000L,
                    rewardClanTokens = 700L
                ),
                OutpostParticipant(
                    playerId = "p_elucidator",
                    playerName = "Elucidator",
                    playerLevel = 27,
                    clanName = "SwordArt",
                    clanLevel = 12,
                    influence = 120_000_000L,
                    rank = 4,
                    powerContribution = 16_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 20_000_000L,
                    rewardWood = 18_000_000L,
                    rewardStone = 16_000_000L,
                    rewardClanTokens = 550L
                ),
                OutpostParticipant(
                    playerId = "p_jabba2",
                    playerName = "Battle Jabba",
                    playerLevel = 34,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 100_000_000L,
                    rank = 5,
                    powerContribution = 14_000_000L,
                    contributionPercent = "(95%)",
                    rewardFood = 18_000_000L,
                    rewardWood = 16_000_000L,
                    rewardStone = 14_000_000L,
                    rewardClanTokens = 450L
                ),
                OutpostParticipant(
                    playerId = "p_asuna",
                    playerName = "AsunaRapier",
                    playerLevel = 26,
                    clanName = "SwordArt",
                    clanLevel = 12,
                    influence = 96_000_000L,
                    rank = 6,
                    powerContribution = 13_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 16_000_000L,
                    rewardWood = 14_000_000L,
                    rewardStone = 12_000_000L,
                    rewardClanTokens = 400L
                )
            )
        ),

        // Осада 4: Оплот
        OutpostSiege(
            id = "siege_oplot_20261007_184024",
            date = "07.10.2026, 18:40:24",
            timestamp = 1791388824000L,
            outpostLevel = 1,
            outpostName = "Оплот",
            outpostType = "Экономический",
            totalPowerSent = 705_000_000L,
            totalPowerAvailable = 800_000_000L,
            powerPercentSent = "88%",
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
                OutpostParticipant(
                    playerId = "p_leo",
                    playerName = "Leonidas_300",
                    playerLevel = 35,
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    influence = 280_000_000L,
                    rank = 1,
                    powerContribution = 35_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 45_000_000L,
                    rewardWood = 40_000_000L,
                    rewardStone = 50_000_000L,
                    rewardClanTokens = 1500L
                ),
                OutpostParticipant(
                    playerId = "p_hoplite",
                    playerName = "HopliteCommander",
                    playerLevel = 31,
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    influence = 190_000_000L,
                    rank = 2,
                    powerContribution = 24_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 30_000_000L,
                    rewardWood = 28_000_000L,
                    rewardStone = 35_000_000L,
                    rewardClanTokens = 950L
                ),
                OutpostParticipant(
                    playerId = "p_phalanx",
                    playerName = "SpartanPhalanx",
                    playerLevel = 29,
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    influence = 150_000_000L,
                    rank = 3,
                    powerContribution = 19_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 24_000_000L,
                    rewardWood = 22_000_000L,
                    rewardStone = 28_000_000L,
                    rewardClanTokens = 750L
                ),
                OutpostParticipant(
                    playerId = "p_eclipse",
                    playerName = "EclipseRider",
                    playerLevel = 27,
                    clanName = "Сумеречный патруль",
                    clanLevel = 17,
                    influence = 85_000_000L,
                    rank = 4,
                    powerContribution = 11_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 14_000_000L,
                    rewardWood = 12_000_000L,
                    rewardStone = 15_000_000L,
                    rewardClanTokens = 300L
                )
            )
        ),

        // Осада 5: Цитадель (3 клана!)
        OutpostSiege(
            id = "siege_citadel_20261007_143000",
            date = "07.10.2026, 14:30:00",
            timestamp = 1791373800000L,
            outpostLevel = 2,
            outpostName = "Цитадель",
            outpostType = "Боевые действия",
            totalPowerSent = 1_740_000_000L,
            totalPowerAvailable = 1_900_000_000L,
            powerPercentSent = "91%",
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
                OutpostParticipant(
                    playerId = "p_awolf2",
                    playerName = "AlphaWolf_77",
                    playerLevel = 32,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 350_000_000L,
                    rank = 1,
                    powerContribution = 38_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 55_000_000L,
                    rewardWood = 50_000_000L,
                    rewardStone = 45_000_000L,
                    rewardClanTokens = 1800L
                ),
                OutpostParticipant(
                    playerId = "p_leo2",
                    playerName = "Leonidas_300",
                    playerLevel = 35,
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    influence = 310_000_000L,
                    rank = 2,
                    powerContribution = 34_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 48_000_000L,
                    rewardWood = 44_000_000L,
                    rewardStone = 52_000_000L,
                    rewardClanTokens = 1500L
                ),
                OutpostParticipant(
                    playerId = "p_cfang2",
                    playerName = "CyberFang",
                    playerLevel = 30,
                    clanName = "Crypto Wolf",
                    clanLevel = 19,
                    influence = 240_000_000L,
                    rank = 3,
                    powerContribution = 26_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 38_000_000L,
                    rewardWood = 34_000_000L,
                    rewardStone = 30_000_000L,
                    rewardClanTokens = 1100L
                ),
                OutpostParticipant(
                    playerId = "p_kirito2",
                    playerName = "Kirito_Slash",
                    playerLevel = 28,
                    clanName = "SwordArt",
                    clanLevel = 12,
                    influence = 190_000_000L,
                    rank = 4,
                    powerContribution = 21_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 29_000_000L,
                    rewardWood = 26_000_000L,
                    rewardStone = 24_000_000L,
                    rewardClanTokens = 850L
                ),
                OutpostParticipant(
                    playerId = "p_hoplite2",
                    playerName = "HopliteCommander",
                    playerLevel = 31,
                    clanName = "⚔️ The_Sparta ⚔️",
                    clanLevel = 23,
                    influence = 180_000_000L,
                    rank = 5,
                    powerContribution = 20_000_000L,
                    contributionPercent = "(100%)",
                    rewardFood = 28_000_000L,
                    rewardWood = 25_000_000L,
                    rewardStone = 32_000_000L,
                    rewardClanTokens = 800L
                )
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
                    obj.has("outposts") -> obj.getJSONArray("outposts")
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
        val outpostLevel = obj.optInt("outpostLevel", obj.optInt("level", 1))
        val outpostName = obj.optString("outpostName", obj.optString("name", "Форт"))
        val outpostType = obj.optString("outpostType", obj.optString("type", "Логистика"))
        val isCompleted = obj.optBoolean("isCompleted", true)

        val totalPowerSent = obj.optLong("totalPowerSent", obj.optLong("clanPowerSent", 450_710_761L))
        val totalPowerAvailable = obj.optLong("totalPowerAvailable", obj.optLong("availablePower", 542_403_000L))
        val powerPercentSent = obj.optString("powerPercentSent", obj.optString("powerPercent", "83%"))

        val myRewardInfluence = obj.optLong("myRewardInfluence", 16_053_688L)
        val myRewardFood = obj.optLong("myRewardFood", 933_000L)
        val myRewardWood = obj.optLong("myRewardWood", 933_000L)
        val myRewardStone = obj.optLong("myRewardStone", 725_000L)
        val myRewardExp = obj.optLong("myRewardExp", 36_000L)
        val myRewardStars = obj.optLong("myRewardStars", 413L)
        val myRewardChests = obj.optLong("myRewardChests", 6L)

        val clansList = mutableListOf<OutpostClan>()
        val clansArr = obj.optJSONArray("clans")
        if (clansArr != null) {
            for (j in 0 until clansArr.length()) {
                val c = clansArr.getJSONObject(j)
                clansList.add(
                    OutpostClan(
                        clanId = c.optString("clanId", "clan_$j"),
                        clanName = c.optString("clanName", c.optString("name", "Клан $j")),
                        clanLevel = c.optInt("clanLevel", c.optInt("level", 1)),
                        score = c.optLong("score", 0L),
                        isWinner = c.optBoolean("isWinner", false),
                        iconType = c.optString("iconType", "default")
                    )
                )
            }
        }

        val participantsList = mutableListOf<OutpostParticipant>()
        val partArr = obj.optJSONArray("participants") ?: obj.optJSONArray("members")
        if (partArr != null) {
            for (k in 0 until partArr.length()) {
                val p = partArr.getJSONObject(k)
                participantsList.add(
                    OutpostParticipant(
                        playerId = p.optString("playerId", p.optString("id", "p_$k")),
                        playerName = p.optString("playerName", p.optString("name", "Игрок $k")),
                        playerLevel = p.optInt("playerLevel", p.optInt("level", 1)),
                        clanName = p.optString("clanName", p.optString("clan", "")),
                        clanLevel = p.optInt("clanLevel", 1),
                        influence = p.optLong("influence", 0L),
                        rank = p.optInt("rank", k + 1),
                        powerContribution = p.optLong("powerContribution", p.optLong("power", p.optLong("contribution", 0L))),
                        contributionPercent = p.optString("contributionPercent", p.optString("percent", "(100%)")),
                        rewardFood = p.optLong("rewardFood", p.optLong("food", 0L)),
                        rewardWood = p.optLong("rewardWood", p.optLong("wood", 0L)),
                        rewardStone = p.optLong("rewardStone", p.optLong("stone", 0L)),
                        rewardGem = p.optLong("rewardGem", p.optLong("gems", 0L)),
                        rewardClanTokens = p.optLong("rewardClanTokens", p.optLong("tokens", 0L)),
                        expReward = p.optLong("expReward", 0L),
                        starsReward = p.optLong("starsReward", 0L),
                        chestsReward = p.optLong("chestsReward", 0L)
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
            totalPowerSent = totalPowerSent,
            totalPowerAvailable = totalPowerAvailable,
            powerPercentSent = powerPercentSent,
            myRewardInfluence = myRewardInfluence,
            myRewardFood = myRewardFood,
            myRewardWood = myRewardWood,
            myRewardStone = myRewardStone,
            myRewardExp = myRewardExp,
            myRewardStars = myRewardStars,
            myRewardChests = myRewardChests,
            participants = participantsList
        )
    }
}
