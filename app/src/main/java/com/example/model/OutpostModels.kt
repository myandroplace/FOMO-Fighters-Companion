package com.example.model

data class OutpostClan(
    val clanId: String,
    val clanName: String,
    val clanLevel: Int,
    val score: Long,
    val isWinner: Boolean = false,
    val iconType: String = "default"
) {
    val tag: String
        get() = "[$clanLevel]$clanName"

    val scoreFormatted: String
        get() = formatPowerOrScore(score)
}

data class OutpostParticipant(
    val playerId: String,
    val playerName: String,
    val playerLevel: Int = 1,
    val clanName: String,
    val clanLevel: Int = 1,
    val influence: Long, // Влияние (шлем) e.g. 294_000_000L
    val rank: Int = 1,
    val powerContribution: Long = 0L, // Вклад мощность ⚔️ e.g. 34_000_000L
    val contributionPercent: String = "100%", // e.g. "(100%)", "(94%)"
    val rewardFood: Long = 0L, // e.g. 47_000_000L
    val rewardWood: Long = 0L,
    val rewardStone: Long = 0L,
    val rewardGem: Long = 0L,
    val rewardClanTokens: Long = 0L,
    val expReward: Long = 0L,
    val starsReward: Long = 0L,
    val chestsReward: Long = 0L,
    val avatarType: String = "warrior"
) {
    val clanTag: String
        get() = "[$clanLevel]$clanName"

    val playerTag: String
        get() = "[$playerLevel] $playerName"

    val influenceFormatted: String
        get() = formatPowerOrScore(influence)

    val powerFormatted: String
        get() = if (powerContribution > 0) formatPowerOrScore(powerContribution) else "-"

    val totalRewardResources: Long
        get() = rewardFood + rewardWood + rewardStone + rewardGem
}

data class OutpostSiege(
    val id: String,
    val date: String, // e.g. "07.10.2026, 21:17:08"
    val timestamp: Long,
    val outpostLevel: Int = 1,
    val outpostName: String, // "Форт", "Донжон", "Твердыня", "Оплот"
    val outpostType: String, // "Логистика", "Боевые действия", "Экономический"
    val clans: List<OutpostClan>,
    val isCompleted: Boolean = true,
    val totalPowerSent: Long = 450_710_761L,
    val totalPowerAvailable: Long = 542_403_000L,
    val powerPercentSent: String = "83%",
    val myRewardInfluence: Long = 16_053_688L,
    val myRewardFood: Long = 933_000L,
    val myRewardWood: Long = 933_000L,
    val myRewardStone: Long = 725_000L,
    val myRewardExp: Long = 36_000L,
    val myRewardStars: Long = 413L,
    val myRewardChests: Long = 6L,
    val participants: List<OutpostParticipant> = emptyList()
) {
    val winnerClan: OutpostClan?
        get() = clans.firstOrNull { it.isWinner } ?: clans.maxByOrNull { it.score }

    val attackerClan: OutpostClan?
        get() = clans.getOrNull(0)

    val defenderClan: OutpostClan?
        get() = clans.getOrNull(1)

    val participatingClansCount: Int
        get() = clans.size

    val totalSiegeScore: Long
        get() = clans.sumOf { it.score }

    val totalParticipantsCount: Int
        get() = participants.size
}

data class GoogleSheetConfig(
    val sheetUrl: String = "https://docs.google.com/spreadsheets/d/your-sheet-id-here",
    val webhookUrl: String = "",
    val autoSyncOnNewData: Boolean = false,
    val lastSyncTimestamp: Long = 0L
)

fun formatPowerOrScore(score: Long): String {
    return when {
        score >= 1_000_000_000L -> {
            val billions = score / 1_000_000_000.0
            if (billions >= 10.0 || score % 1_000_000_000L == 0L) {
                String.format(java.util.Locale.US, "%.0fB", billions)
            } else {
                String.format(java.util.Locale.US, "%.1fB", billions)
            }
        }
        score >= 1_000_000L -> {
            val millions = score / 1_000_000.0
            if (millions >= 10.0 || score % 1_000_000L == 0L) {
                String.format(java.util.Locale.US, "%.0fM", millions)
            } else {
                String.format(java.util.Locale.US, "%.1fM", millions)
            }
        }
        score >= 1_000L -> {
            String.format(java.util.Locale.US, "%.0fK", score / 1_000.0)
        }
        else -> score.toString()
    }
}
