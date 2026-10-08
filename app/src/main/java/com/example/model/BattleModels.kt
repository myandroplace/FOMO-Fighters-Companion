package com.example.model

data class DropItem(
    val type: String,
    val count: Int,
    val data: Int? = null
)

data class BattleLog(
    val id: String,
    val attackerId: String,
    val targetId: String,
    val timerId: String? = null,
    val battleType: String = "attack",
    val attackerName: String = "",
    val attackerRace: String = "",
    val attackerLevel: Int = 1,
    val attackerPower: Long = 0,
    val attackerAtk: Long = 0,
    val attackerTroopsBefore: Map<String, Int> = emptyMap(),
    val attackerTroopsAfter: Map<String, Int> = emptyMap(),
    val attackerExp: Long = 0,
    val attackerLooseAtk: Long = 0,
    val attackerLooseDef: Long = 0,
    val attackerLooseTroopsCost: Long = 0,
    val attackerEfficiency: Int = 1, // -1, 1, 2, 3 stars
    val attackerFood: Long = 0,
    val attackerWood: Long = 0,
    val attackerStone: Long = 0,
    val attackerGem: Long = 0,
    val targetType: String = "camp",
    val targetName: String = "",
    val targetRace: String = "",
    val targetLevel: Int = 0,
    val targetPower: Long = 0,
    val targetLooseAtk: Long = 0,
    val targetLooseDef: Long = 0,
    val targetLooseTroopsCost: Long = 0,
    val targetDef: Long = 0,
    val targetExp: Long = 0,
    val targetTroopsBefore: Map<String, Int> = emptyMap(),
    val targetTroopsAfter: Map<String, Int> = emptyMap(),
    val creationDate: String = "",
    val isScoutSuccess: Boolean = false,
    val isScout: Boolean = false,
    val attackerItems: List<DropItem> = emptyList(),
    val attackerAvatar: String? = null,
    val isRead: Boolean = false
) {
    val totalLoot: Long
        get() = attackerFood + attackerWood + attackerStone + attackerGem

    val netProfit: Long
        get() = totalLoot - attackerLooseTroopsCost

    val roiPercent: Double
        get() = if (attackerLooseTroopsCost > 0) {
            ((totalLoot.toDouble() - attackerLooseTroopsCost.toDouble()) / attackerLooseTroopsCost.toDouble()) * 100.0
        } else if (totalLoot > 0) {
            100.0
        } else {
            0.0
        }

    val leadExp: Int
        get() = attackerItems.firstOrNull { it.type == "lead_exp" }?.count ?: 0

    val leadStarExp: Int
        get() = attackerItems.firstOrNull { it.type == "lead_star_exp" }?.count ?: 0

    val troopLosses: Map<String, Int>
        get() {
            val losses = mutableMapOf<String, Int>()
            attackerTroopsBefore.forEach { (unit, before) ->
                val after = attackerTroopsAfter[unit] ?: 0
                val lost = before - after
                if (lost > 0) {
                    losses[unit] = lost
                }
            }
            return losses
        }

    val targetTroopLosses: Map<String, Int>
        get() {
            val losses = mutableMapOf<String, Int>()
            targetTroopsBefore.forEach { (unit, before) ->
                val after = targetTroopsAfter[unit] ?: 0
                val lost = before - after
                if (lost > 0) {
                    losses[unit] = lost
                }
            }
            return losses
        }
}

data class ApiConfig(
    val apiUrl: String = "https://api2.fomofighters.xyz/battle/logs/my",
    val apiKey: String = "85ec9abfe155915fd047fb9e5595a9dc948abc98449ef2bd1b0877da252abf80",
    val apiHash: String = "0b0e51178cddb222cb852f795c4910da",
    val apiTime: String = "1791296910",
    val apiVersion: String = "7d3302bdf4620e43bf7dc2146bf02017",
    val origin: String = "https://play.fomofighters.xyz",
    val referer: String = "https://play.fomofighters.xyz/",
    val userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/154.0.0.0 Safari/537.36"
)

data class CampSiegeSummary(
    val targetId: String,
    val targetRace: String,
    val targetLevel: Int,
    val targetType: String,
    val waveCount: Int,
    val totalFood: Long,
    val totalWood: Long,
    val totalStone: Long,
    val totalGem: Long,
    val totalLoot: Long,
    val totalLossCost: Long,
    val netProfit: Long,
    val totalHeroExp: Int,
    val totalStarExp: Int,
    val isCleared: Boolean,
    val firstAttackDate: String,
    val lastAttackDate: String,
    val battles: List<BattleLog>
)
