package com.example.calculator

import com.example.analytics.BattleAnalytics
import com.example.model.BattleLog

data class RaidSimulationResult(
    val recommendedAtk: Long,
    val recommendedTroops: Map<String, Int>,
    val estimatedWaves: Int,
    val estimatedLootTotal: Long,
    val estimatedFood: Long,
    val estimatedWood: Long,
    val estimatedStone: Long,
    val estimatedLossCost: Long,
    val estimatedNetProfit: Long,
    val estimatedHeroExp: Int,
    val recommendedCompositionAdvice: String
)

data class RetrainCost(
    val unitKey: String,
    val unitName: String,
    val countToTrain: Int,
    val foodCost: Long,
    val woodCost: Long,
    val stoneCost: Long,
    val goldCost: Long
)

object RaidCalculator {

    fun simulateRaid(
        race: String,
        level: Int,
        historicalBattles: List<BattleLog>
    ): RaidSimulationResult {
        // Find matching past battles to base real calculations on
        val matching = historicalBattles.filter {
            it.targetRace.equals(race, ignoreCase = true) && it.targetLevel == level
        }

        val avgLoot = if (matching.isNotEmpty()) matching.map { it.totalLoot }.average().toLong() else 65000L
        val avgFood = if (matching.isNotEmpty()) matching.map { it.attackerFood }.average().toLong() else 15000L
        val avgWood = if (matching.isNotEmpty()) matching.map { it.attackerWood }.average().toLong() else 25000L
        val avgStone = if (matching.isNotEmpty()) matching.map { it.attackerStone }.average().toLong() else 25000L
        val avgLoss = if (matching.isNotEmpty()) matching.map { it.attackerLooseTroopsCost }.average().toLong() else 8000L
        val avgHeroExp = if (matching.isNotEmpty()) matching.map { it.leadExp }.average().toInt() else 350

        // Target power benchmarks
        val baseAtk = when (level) {
            10 -> 110000L
            11 -> 125000L
            12 -> 135000L
            else -> 100000L + (level * 2500L)
        }

        val recommendedTroops = mapOf(
            "human_archer_10" to (1900 + (level - 10) * 150),
            "human_barracks_10" to (2100 + (level - 10) * 250),
            "human_barracks_30" to (35 + (level - 10) * 3),
            "human_stable_10" to (200 + (level - 10) * 80)
        )

        val advice = when {
            race.equals("skeleton", ignoreCase = true) && level >= 12 ->
                "⚠️ Скелеты 12 уровня имеют высокую защиту (до 11,285 DEF). Рекомендуется не менее 130,000+ ATK и повышенное число лучников для минимизации потерь пехоты."
            race.equals("goblin", ignoreCase = true) ->
                "💡 Гоблины $level уровня дают высокий дроп камня и дерева при относительно низкой защите. Оптимально для фарма с чистой прибылью!"
            else ->
                "⚔️ Атакуйте полным отрядом (пехота + лучники + конница) для получения рейтинга 3 звезды и снижения цены потерь."
        }

        return RaidSimulationResult(
            recommendedAtk = baseAtk,
            recommendedTroops = recommendedTroops,
            estimatedWaves = if (level >= 12) 2 else 1,
            estimatedLootTotal = avgLoot,
            estimatedFood = avgFood,
            estimatedWood = avgWood,
            estimatedStone = avgStone,
            estimatedLossCost = avgLoss,
            estimatedNetProfit = avgLoot - avgLoss,
            estimatedHeroExp = avgHeroExp,
            recommendedCompositionAdvice = advice
        )
    }

    fun calculateRetrainingCosts(casualties: Map<String, Int>): List<RetrainCost> {
        val list = mutableListOf<RetrainCost>()
        for ((unit, count) in casualties) {
            if (count <= 0) continue
            val name = BattleAnalytics.getUnitDisplayName(unit)

            // Approximate training resource costs per unit based on FOMO Fighters tiers
            val (food, wood, stone, gold) = when (unit) {
                "human_archer_10" -> Quad(10L, 25L, 5L, 25L)
                "human_barracks_10" -> Quad(20L, 10L, 10L, 25L)
                "human_barracks_30" -> Quad(80L, 50L, 50L, 120L)
                "human_stable_10" -> Quad(30L, 15L, 20L, 45L)
                "human_siege_30" -> Quad(40L, 100L, 70L, 150L)
                else -> Quad(15L, 15L, 10L, 20L)
            }

            list.add(
                RetrainCost(
                    unitKey = unit,
                    unitName = name,
                    countToTrain = count,
                    foodCost = food * count,
                    woodCost = wood * count,
                    stoneCost = stone * count,
                    goldCost = gold * count
                )
            )
        }
        return list
    }

    private data class Quad(val a: Long, val b: Long, val c: Long, val d: Long)
}
