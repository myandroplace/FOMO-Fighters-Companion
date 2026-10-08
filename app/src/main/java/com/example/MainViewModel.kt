package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.BattleAnalytics
import com.example.analytics.CampTypeStats
import com.example.analytics.OverallStats
import com.example.calculator.RaidCalculator
import com.example.calculator.RaidSimulationResult
import com.example.calculator.RetrainCost
import com.example.data.BattleRepository
import com.example.data.GoogleSheetsExporter
import com.example.data.OutpostData
import com.example.model.ApiConfig
import com.example.model.BattleLog
import com.example.model.CampSiegeSummary
import com.example.model.GoogleSheetConfig
import com.example.model.OutpostSiege
import com.example.network.FomoFightersApi
import com.example.ui.components.formatCompactNumber
import com.example.ui.components.formatNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BattleRepository(application)
    private val prefs = application.getSharedPreferences("fomo_sheets_prefs", Context.MODE_PRIVATE)

    val battles: StateFlow<List<BattleLog>> = repository.battles
    val apiConfig: StateFlow<ApiConfig> = repository.apiConfig

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterRace = MutableStateFlow<String?>(null)
    val filterRace: StateFlow<String?> = _filterRace.asStateFlow()

    private val _filterEfficiency = MutableStateFlow<Int?>(null)
    val filterEfficiency: StateFlow<Int?> = _filterEfficiency.asStateFlow()

    private val _selectedBattle = MutableStateFlow<BattleLog?>(null)
    val selectedBattle: StateFlow<BattleLog?> = _selectedBattle.asStateFlow()

    private val _selectedCamp = MutableStateFlow<CampSiegeSummary?>(null)
    val selectedCamp: StateFlow<CampSiegeSummary?> = _selectedCamp.asStateFlow()

    // --- Outpost Sieges History (Вкладка Аванпосты - История осад) ---
    private val _outpostSieges = MutableStateFlow<List<OutpostSiege>>(loadOutpostSieges())
    val outpostSieges: StateFlow<List<OutpostSiege>> = _outpostSieges.asStateFlow()

    private val _selectedSiege = MutableStateFlow<OutpostSiege?>(null)
    val selectedSiege: StateFlow<OutpostSiege?> = _selectedSiege.asStateFlow()

    private val _filterOutpostType = MutableStateFlow<String?>(null) // "Логистика", "Боевые действия", "Экономический"
    val filterOutpostType: StateFlow<String?> = _filterOutpostType.asStateFlow()

    private val _filterOutpostClan = MutableStateFlow<String?>(null)
    val filterOutpostClan: StateFlow<String?> = _filterOutpostClan.asStateFlow()

    val filteredOutpostSieges: StateFlow<List<OutpostSiege>> = combine(
        outpostSieges,
        filterOutpostType,
        filterOutpostClan,
        searchQuery
    ) { sieges, typeFilter, clanFilter, query ->
        sieges.filter { s ->
            val matchType = typeFilter == null || s.outpostType.equals(typeFilter, ignoreCase = true)
            val matchClan = clanFilter == null || s.clans.any { it.clanName.contains(clanFilter, ignoreCase = true) }
            val matchQuery = query.isBlank() ||
                    s.outpostName.contains(query, ignoreCase = true) ||
                    s.date.contains(query, ignoreCase = true) ||
                    s.clans.any { it.clanName.contains(query, ignoreCase = true) } ||
                    s.participants.any { it.playerName.contains(query, ignoreCase = true) }
            matchType && matchClan && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Google Sheets Config ---
    private val _sheetConfig = MutableStateFlow(loadSheetConfig())
    val sheetConfig: StateFlow<GoogleSheetConfig> = _sheetConfig.asStateFlow()

    // Overall analytics
    val overallStats: StateFlow<OverallStats> = battles
        .combine(MutableStateFlow(Unit)) { bList, _ ->
            BattleAnalytics.calculateOverallStats(bList)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, BattleAnalytics.calculateOverallStats(emptyList()))

    // Camp sieges grouped by targetId
    val campSieges: StateFlow<List<CampSiegeSummary>> = battles
        .combine(MutableStateFlow(Unit)) { bList, _ ->
            BattleAnalytics.groupCamps(bList)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Camp stats by race & level
    val campTypeStats: StateFlow<List<CampTypeStats>> = battles
        .combine(MutableStateFlow(Unit)) { bList, _ ->
            BattleAnalytics.calculateCampTypeStats(bList)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered battles list
    val filteredBattles: StateFlow<List<BattleLog>> = combine(
        battles,
        searchQuery,
        filterRace,
        filterEfficiency
    ) { list, query, race, eff ->
        list.filter { b ->
            val matchQuery = query.isBlank() ||
                    b.targetRace.contains(query, ignoreCase = true) ||
                    b.targetType.contains(query, ignoreCase = true) ||
                    b.creationDate.contains(query, ignoreCase = true) ||
                    b.targetId.contains(query, ignoreCase = true)
            val matchRace = race == null || b.targetRace.equals(race, ignoreCase = true)
            val matchEff = eff == null || b.attackerEfficiency == eff
            matchQuery && matchRace && matchEff
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun loadOutpostSieges(): List<OutpostSiege> {
        val savedJson = prefs.getString("saved_outposts_json", null)
        return if (!savedJson.isNullOrBlank()) {
            try {
                OutpostData.parseOutpostSiegesJson(savedJson)
            } catch (e: Exception) {
                OutpostData.sampleSieges
            }
        } else {
            OutpostData.sampleSieges
        }
    }

    private fun loadSheetConfig(): GoogleSheetConfig {
        return GoogleSheetConfig(
            sheetUrl = prefs.getString("sheet_url", "https://docs.google.com/spreadsheets/d/your-sheet-id-here") ?: "",
            webhookUrl = prefs.getString("webhook_url", "") ?: "",
            autoSyncOnNewData = prefs.getBoolean("auto_sync", false),
            lastSyncTimestamp = prefs.getLong("last_sync", 0L)
        )
    }

    fun updateSheetConfig(config: GoogleSheetConfig) {
        _sheetConfig.value = config
        prefs.edit()
            .putString("sheet_url", config.sheetUrl)
            .putString("webhook_url", config.webhookUrl)
            .putBoolean("auto_sync", config.autoSyncOnNewData)
            .putLong("last_sync", config.lastSyncTimestamp)
            .apply()
        _statusMessage.value = "Настройки Google Таблицы сохранены"
    }

    fun selectSiege(siege: OutpostSiege?) {
        _selectedSiege.value = siege
    }

    fun setFilterOutpostType(type: String?) {
        _filterOutpostType.value = type
    }

    fun setFilterOutpostClan(clan: String?) {
        _filterOutpostClan.value = clan
    }

    fun importOutpostJson(jsonText: String) {
        try {
            val sieges = OutpostData.parseOutpostSiegesJson(jsonText)
            if (sieges.isNotEmpty()) {
                _outpostSieges.value = sieges
                prefs.edit().putString("saved_outposts_json", jsonText).apply()
                _statusMessage.value = "Успешно загружено ${sieges.size} осад аванпостов!"
            } else {
                _statusMessage.value = "В JSON не найдено данных осад"
            }
        } catch (e: Exception) {
            _statusMessage.value = "Ошибка разбора JSON: ${e.localizedMessage}"
        }
    }

    fun syncToGoogleSheetsWebhook() {
        viewModelScope.launch {
            val webhook = sheetConfig.value.webhookUrl
            if (webhook.isBlank()) {
                _statusMessage.value = "Сначала укажите URL Webhook / Apps Script в настройках"
                return@launch
            }

            _isLoading.value = true
            _statusMessage.value = "Отправка осад в Google Таблицу..."
            val result = GoogleSheetsExporter.sendToWebhook(webhook, outpostSieges.value)
            _isLoading.value = false

            result.onSuccess { msg ->
                val updated = sheetConfig.value.copy(lastSyncTimestamp = System.currentTimeMillis())
                updateSheetConfig(updated)
                _statusMessage.value = msg
            }.onFailure { err ->
                _statusMessage.value = "Ошибка отправки: ${err.localizedMessage ?: "Сетевая ошибка"}"
            }
        }
    }

    fun getSiegesTsv(): String {
        return GoogleSheetsExporter.generateSiegesTsv(outpostSieges.value)
    }

    fun getCompact3ColTsv(): String {
        return GoogleSheetsExporter.generateCompact3ColTsv(outpostSieges.value)
    }

    fun getStandard4ColTsv(): String {
        return GoogleSheetsExporter.generateStandard4ColTsv(outpostSieges.value)
    }

    fun getParticipantsTsv(): String {
        return GoogleSheetsExporter.generateParticipantsTsv(outpostSieges.value)
    }

    fun getSingleSiegeParticipantsTsv(siege: OutpostSiege): String {
        return GoogleSheetsExporter.generateSingleSiegeParticipantsTsv(siege)
    }

    fun getSingleSiegeCompact3ColTsv(siege: OutpostSiege): String {
        return GoogleSheetsExporter.generateCompact3ColTsv(listOf(siege))
    }

    val totalParticipantsCount: Int
        get() = outpostSieges.value.sumOf { it.participants.size }

    fun getAppsScriptTemplate(): String {
        return GoogleSheetsExporter.getAppsScriptTemplate()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterRace(race: String?) {
        _filterRace.value = race
    }

    fun setFilterEfficiency(eff: Int?) {
        _filterEfficiency.value = eff
    }

    fun selectBattle(battle: BattleLog?) {
        _selectedBattle.value = battle
    }

    fun selectCamp(camp: CampSiegeSummary?) {
        _selectedCamp.value = camp
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun updateApiConfig(config: ApiConfig) {
        repository.saveApiConfig(config)
        _statusMessage.value = "Настройки API успешно сохранены"
    }

    fun resetToDefaults() {
        repository.resetToDefaults()
        _outpostSieges.value = OutpostData.sampleSieges
        prefs.edit().remove("saved_outposts_json").apply()
        _statusMessage.value = "Данные сброшены до стандартных"
    }

    fun fetchBattleLogsFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Подключение к api2.fomofighters.xyz..."

            val result = FomoFightersApi.fetchBattleLogs(apiConfig.value)
            _isLoading.value = false

            result.onSuccess { newLogs ->
                if (newLogs.isNotEmpty()) {
                    repository.appendOrUpdateBattles(newLogs)
                    _statusMessage.value = "Успешно получено ${newLogs.size} логов боев!"
                } else {
                    _statusMessage.value = "Ответ успешен, но список логов пуст"
                }
            }.onFailure { error ->
                _statusMessage.value = "Ошибка запроса API: ${error.localizedMessage ?: "Сетевая ошибка"}"
            }
        }
    }

    fun importRawJson(jsonText: String) {
        val trimmed = jsonText.trim()
        if (trimmed.contains("outpost") || trimmed.contains("siege") || trimmed.contains("Форт") || trimmed.contains("Донжон") || trimmed.contains("Твердыня") || trimmed.contains("participants")) {
            try {
                val sieges = OutpostData.parseOutpostSiegesJson(jsonText)
                if (sieges.isNotEmpty()) {
                    _outpostSieges.value = sieges
                    prefs.edit().putString("saved_outposts_json", jsonText).apply()
                    _statusMessage.value = "Успешно импортировано ${sieges.size} осад и ${sieges.sumOf { it.participants.size }} участников аванпостов!"
                    return
                }
            } catch (e: Exception) {
                // Fallback to battle logs below
            }
        }
        try {
            val logs = FomoFightersApi.parseBattleLogsJson(jsonText)
            if (logs.isNotEmpty()) {
                repository.updateBattles(logs, jsonText)
                _statusMessage.value = "Импортировано ${logs.size} логов боев!"
            } else {
                _statusMessage.value = "В JSON не найдено логов"
            }
        } catch (e: Exception) {
            _statusMessage.value = "Ошибка парсинга JSON: ${e.localizedMessage}"
        }
    }

    fun parseAndApplyCurl(curlText: String) {
        try {
            val updated = FomoFightersApi.parseCurlCommand(curlText, apiConfig.value)
            repository.saveApiConfig(updated)
            _statusMessage.value = "Заголовки и ключи из cURL успешно распознаны!"
        } catch (e: Exception) {
            _statusMessage.value = "Ошибка разбора cURL: ${e.localizedMessage}"
        }
    }

    fun simulateRaid(race: String, level: Int): RaidSimulationResult {
        return RaidCalculator.simulateRaid(race, level, battles.value)
    }

    fun getRetrainCosts(): List<RetrainCost> {
        val casualties = overallStats.value.troopCasualties
        return RaidCalculator.calculateRetrainingCosts(casualties)
    }

    fun generateClanReport(): String {
        val stats = overallStats.value
        val sieges = outpostSieges.value
        val player = battles.value.firstOrNull()?.attackerName ?: "Bob Marley Money Mining"

        return buildString {
            append("⚔️ *ОТЧЕТ АВАНПОСТОВ И РЕЙДОВ: FOMO FIGHTERS* ⚔️\n")
            append("👤 Игрок: $player\n")
            append("🏰 Зафиксировано осад аванпостов: ${sieges.size}\n")
            append("📊 Боев в личной истории: ${stats.totalBattles}\n\n")
            append("🚩 *ПОСЛЕДНИЕ ОСАДЫ АВАНПОСТОВ:*\n")
            sieges.take(3).forEach { s ->
                val win = s.winnerClan?.tag ?: "Ничья"
                append("• ${s.outpostName} (${s.outpostType}) [${s.date}]: Победил $win (${s.winnerClan?.scoreFormatted})\n")
            }
            append("\n💰 *ДОБЫЧА РЕСУРСОВ В РЕЙДАХ:*\n")
            append("🌾 Еда: ${formatNumber(stats.totalFood)}\n")
            append("🪵 Дерево: ${formatNumber(stats.totalWood)}\n")
            append("🪨 Камень: ${formatNumber(stats.totalStone)}\n")
            append("📈 ЧИСТАЯ ПРИБЫЛЬ: +${formatNumber(stats.netProfit)} (ROI ${String.format("%.1f", stats.overallRoiPercent)}%)\n")
        }
    }
}
