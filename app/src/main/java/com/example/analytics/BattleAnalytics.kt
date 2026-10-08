package com.example.analytics

import com.example.model.BattleLog
import com.example.model.CampSiegeSummary

data class OverallStats(
    val totalBattles: Int,
    val totalFood: Long,
    val totalWood: Long,
    val totalStone: Long,
    val totalGem: Long,
    val totalGrossLoot: Long,
    val totalTroopLossCost: Long,
    val netProfit: Long,
    val overallRoiPercent: Double,
    val totalHeroExp: Int,
    val totalStarExp: Int,
    val star3Count: Int,
    val star2Count: Int,
    val star1Count: Int,
    val starMinus1Count: Int,
    val averageEfficiency: Double,
    val totalLostAtk: Long,
    val totalLostDef: Long,
    val troopCasualties: Map<String, Int>,
    val targetCasualties: Map<String, Int>
)

data class CampTypeStats(
    val race: String,
    val level: Int,
    val attackCount: Int,
    val campsCount: Int,
    val totalLoot: Long,
    val totalLossCost: Long,
    val netProfit: Long,
    val avgLootPerAttack: Long,
    val avgNetPerAttack: Long,
    val avgHeroExpPerAttack: Double,
    val avgEfficiency: Double
)

object BattleAnalytics {

    fun calculateOverallStats(battles: List<BattleLog>): OverallStats {
        if (battles.isEmpty()) {
            return OverallStats(
                totalBattles = 0,
                totalFood = 0,
                totalWood = 0,
                totalStone = 0,
                totalGem = 0,
                totalGrossLoot = 0,
                totalTroopLossCost = 0,
                netProfit = 0,
                overallRoiPercent = 0.0,
                totalHeroExp = 0,
                totalStarExp = 0,
                star3Count = 0,
                star2Count = 0,
                star1Count = 0,
                starMinus1Count = 0,
                averageEfficiency = 0.0,
                totalLostAtk = 0,
                totalLostDef = 0,
                troopCasualties = emptyMap(),
                targetCasualties = emptyMap()
            )
        }

        var food = 0L
        var wood = 0L
        var stone = 0L
        var gem = 0L
        var lossCost = 0L
        var heroExp = 0
        var starExp = 0
        var star3 = 0
        var star2 = 0
        var star1 = 0
        var starMinus1 = 0
        var efficiencySum = 0.0
        var lostAtk = 0L
        var lostDef = 0L
        val troopCasualties = mutableMapOf<String, Int>()
        val targetCasualties = mutableMapOf<String, Int>()

        for (b in battles) {
            food += b.attackerFood
            wood += b.attackerWood
            stone += b.attackerStone
            gem += b.attackerGem
            lossCost += b.attackerLooseTroopsCost
            heroExp += b.leadExp
            starExp += b.leadStarExp

            when (b.attackerEfficiency) {
                3 -> star3++
                2 -> star2++
                1 -> star1++
                else -> starMinus1++
            }
            efficiencySum += b.attackerEfficiency
            lostAtk += b.attackerLooseAtk
            lostDef += b.attackerLooseDef

            b.troopLosses.forEach { (unit, count) ->
                troopCasualties[unit] = (troopCasualties[unit] ?: 0) + count
            }
            b.targetTroopLosses.forEach { (unit, count) ->
                targetCasualties[unit] = (targetCasualties[unit] ?: 0) + count
            }
        }

        val totalGross = food + wood + stone + gem
        val net = totalGross - lossCost
        val roi = if (lossCost > 0) ((net.toDouble() / lossCost.toDouble()) * 100.0) else 100.0

        return OverallStats(
            totalBattles = battles.size,
            totalFood = food,
            totalWood = wood,
            totalStone = stone,
            totalGem = gem,
            totalGrossLoot = totalGross,
            totalTroopLossCost = lossCost,
            netProfit = net,
            overallRoiPercent = roi,
            totalHeroExp = heroExp,
            totalStarExp = starExp,
            star3Count = star3,
            star2Count = star2,
            star1Count = star1,
            starMinus1Count = starMinus1,
            averageEfficiency = efficiencySum / battles.size,
            totalLostAtk = lostAtk,
            totalLostDef = lostDef,
            troopCasualties = troopCasualties,
            targetCasualties = targetCasualties
        )
    }

    fun groupCamps(battles: List<BattleLog>): List<CampSiegeSummary> {
        val grouped = battles.groupBy { it.targetId }
        val summaries = mutableListOf<CampSiegeSummary>()

        for ((targetId, campBattles) in grouped) {
            val sorted = campBattles.sortedBy { it.creationDate }
            val first = sorted.first()
            val last = sorted.last()

            var totalFood = 0L
            var totalWood = 0L
            var totalStone = 0L
            var totalGem = 0L
            var totalLossCost = 0L
            var heroExp = 0
            var starExp = 0

            for (b in campBattles) {
                totalFood += b.attackerFood
                totalWood += b.attackerWood
                totalStone += b.attackerStone
                totalGem += b.attackerGem
                totalLossCost += b.attackerLooseTroopsCost
                heroExp += b.leadExp
                starExp += b.leadStarExp
            }

            val totalLoot = totalFood + totalWood + totalStone + totalGem
            val net = totalLoot - totalLossCost

            // If in the latest battle, targetTroopsAfter is empty or all 0, or targetPower == 0, it was cleared
            val isCleared = last.targetTroopsAfter.isEmpty() ||
                    last.targetTroopsAfter.values.all { it == 0 } ||
                    last.targetPower == 0L

            summaries.add(
                CampSiegeSummary(
                    targetId = targetId,
                    targetRace = first.targetRace.ifBlank { "unknown" },
                    targetLevel = first.targetLevel,
                    targetType = first.targetType,
                    waveCount = campBattles.size,
                    totalFood = totalFood,
                    totalWood = totalWood,
                    totalStone = totalStone,
                    totalGem = totalGem,
                    totalLoot = totalLoot,
                    totalLossCost = totalLossCost,
                    netProfit = net,
                    totalHeroExp = heroExp,
                    totalStarExp = starExp,
                    isCleared = isCleared,
                    firstAttackDate = first.creationDate,
                    lastAttackDate = last.creationDate,
                    battles = sorted.reversed() // most recent first
                )
            )
        }

        // Sort by last attack date descending
        return summaries.sortedByDescending { it.lastAttackDate }
    }

    fun calculateCampTypeStats(battles: List<BattleLog>): List<CampTypeStats> {
        val groups = battles.groupBy { "${it.targetRace.lowercase()}_${it.targetLevel}" }
        val list = mutableListOf<CampTypeStats>()

        for ((_, bList) in groups) {
            val first = bList.first()
            val race = first.targetRace.ifBlank { "Враг" }
            val level = first.targetLevel
            val uniqueCamps = bList.map { it.targetId }.distinct().size

            val totalLoot = bList.sumOf { it.totalLoot }
            val totalLoss = bList.sumOf { it.attackerLooseTroopsCost }
            val net = totalLoot - totalLoss
            val totalHeroExp = bList.sumOf { it.leadExp }
            val avgEfficiency = bList.map { it.attackerEfficiency }.average()

            list.add(
                CampTypeStats(
                    race = race,
                    level = level,
                    attackCount = bList.size,
                    campsCount = uniqueCamps,
                    totalLoot = totalLoot,
                    totalLossCost = totalLoss,
                    netProfit = net,
                    avgLootPerAttack = totalLoot / bList.size,
                    avgNetPerAttack = net / bList.size,
                    avgHeroExpPerAttack = totalHeroExp.toDouble() / bList.size,
                    avgEfficiency = avgEfficiency
                )
            )
        }

        return list.sortedByDescending { it.avgNetPerAttack }
    }

    fun getUnitDisplayName(unitKey: String): String {
        return when (unitKey) {
            "human_archer_10" -> "Лучники (Т1)"
            "human_barracks_10" -> "Пехотинцы (Т1)"
            "human_barracks_30" -> "Гвардейцы (Т3)"
            "human_stable_10" -> "Кавалерия (Т1)"
            "human_siege_30" -> "Осадные баллисты (Т3)"
            "skeleton_10" -> "Скелеты-пехотинцы"
            "skeleton_20" -> "Скелеты-лучники"
            "skeleton_30" -> "Скелеты-стражи"
            "goblin_10" -> "Гоблины-налетчики"
            "goblin_20" -> "Гоблины-стрелки"
            "goblin_30" -> "Гоблины-шаманы"
            else -> unitKey.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
    }
}
