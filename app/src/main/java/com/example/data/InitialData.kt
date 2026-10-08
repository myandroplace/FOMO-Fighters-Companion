package com.example.data

import android.content.Context
import com.example.model.BattleLog
import com.example.network.FomoFightersApi

object InitialData {

    private var cachedBattles: List<BattleLog>? = null

    fun getInitialBattles(context: Context): List<BattleLog> {
        cachedBattles?.let { return it }

        return try {
            val jsonText = context.assets.open("initial_battles.json").bufferedReader().use { it.readText() }
            val list = FomoFightersApi.parseBattleLogsJson(jsonText)
            cachedBattles = list
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getInitialJson(context: Context): String {
        return try {
            context.assets.open("initial_battles.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "{\"success\":true,\"data\":[]}"
        }
    }
}
