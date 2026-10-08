package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ApiConfig
import com.example.model.BattleLog
import com.example.network.FomoFightersApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BattleRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fomo_fighters_prefs", Context.MODE_PRIVATE)

    private val _battles = MutableStateFlow<List<BattleLog>>(emptyList())
    val battles: StateFlow<List<BattleLog>> = _battles.asStateFlow()

    private val _apiConfig = MutableStateFlow(loadApiConfig())
    val apiConfig: StateFlow<ApiConfig> = _apiConfig.asStateFlow()

    init {
        loadBattles()
    }

    private fun loadBattles() {
        val savedJson = prefs.getString(KEY_BATTLES_JSON, null)
        val loadedList = if (!savedJson.isNullOrBlank()) {
            try {
                FomoFightersApi.parseBattleLogsJson(savedJson)
            } catch (e: Exception) {
                InitialData.getInitialBattles(context)
            }
        } else {
            InitialData.getInitialBattles(context)
        }
        _battles.value = loadedList
    }

    fun updateBattles(newList: List<BattleLog>, saveRawJson: String? = null) {
        _battles.value = newList
        val jsonToSave = saveRawJson ?: InitialData.getInitialJson(context)
        prefs.edit().putString(KEY_BATTLES_JSON, jsonToSave).apply()
    }

    fun appendOrUpdateBattles(newBattles: List<BattleLog>) {
        val current = _battles.value.toMutableList()
        val currentIds = current.map { it.id }.toSet()
        val toAdd = newBattles.filter { it.id !in currentIds }
        val combined = toAdd + current
        _battles.value = combined
    }

    fun resetToDefaults() {
        prefs.edit().remove(KEY_BATTLES_JSON).apply()
        _battles.value = InitialData.getInitialBattles(context)
        val defaultConfig = ApiConfig()
        saveApiConfig(defaultConfig)
    }

    fun saveApiConfig(config: ApiConfig) {
        _apiConfig.value = config
        prefs.edit()
            .putString(KEY_API_URL, config.apiUrl)
            .putString(KEY_API_KEY, config.apiKey)
            .putString(KEY_API_HASH, config.apiHash)
            .putString(KEY_API_TIME, config.apiTime)
            .putString(KEY_API_VERSION, config.apiVersion)
            .putString(KEY_ORIGIN, config.origin)
            .putString(KEY_REFERER, config.referer)
            .putString(KEY_USER_AGENT, config.userAgent)
            .apply()
    }

    private fun loadApiConfig(): ApiConfig {
        return ApiConfig(
            apiUrl = prefs.getString(KEY_API_URL, "https://api2.fomofighters.xyz/battle/logs/my") ?: "https://api2.fomofighters.xyz/battle/logs/my",
            apiKey = prefs.getString(KEY_API_KEY, "85ec9abfe155915fd047fb9e5595a9dc948abc98449ef2bd1b0877da252abf80") ?: "85ec9abfe155915fd047fb9e5595a9dc948abc98449ef2bd1b0877da252abf80",
            apiHash = prefs.getString(KEY_API_HASH, "0b0e51178cddb222cb852f795c4910da") ?: "0b0e51178cddb222cb852f795c4910da",
            apiTime = prefs.getString(KEY_API_TIME, "1791296910") ?: "1791296910",
            apiVersion = prefs.getString(KEY_API_VERSION, "7d3302bdf4620e43bf7dc2146bf02017") ?: "7d3302bdf4620e43bf7dc2146bf02017",
            origin = prefs.getString(KEY_ORIGIN, "https://play.fomofighters.xyz") ?: "https://play.fomofighters.xyz",
            referer = prefs.getString(KEY_REFERER, "https://play.fomofighters.xyz/") ?: "https://play.fomofighters.xyz/",
            userAgent = prefs.getString(KEY_USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/154.0.0.0 Safari/537.36") ?: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/154.0.0.0 Safari/537.36"
        )
    }

    companion object {
        private const val KEY_BATTLES_JSON = "battles_raw_json"
        private const val KEY_API_URL = "api_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_API_HASH = "api_hash"
        private const val KEY_API_TIME = "api_time"
        private const val KEY_API_VERSION = "api_version"
        private const val KEY_ORIGIN = "origin"
        private const val KEY_REFERER = "referer"
        private const val KEY_USER_AGENT = "user_agent"
    }
}
