package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.analytics.BattleAnalytics
import com.example.data.GoogleSheetsExporter
import com.example.data.InitialData
import com.example.data.OutpostData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FOMO Fighters", appName)
  }

  @Test
  fun `initial battles parse correctly and produce valid stats`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val battles = InitialData.getInitialBattles(context)
    assertTrue("Battles list should not be empty", battles.isNotEmpty())
    assertEquals(50, battles.size)

    val stats = BattleAnalytics.calculateOverallStats(battles)
    assertTrue("Total gross loot should be greater than 0", stats.totalGrossLoot > 0)
    assertTrue("Net profit should be greater than 0", stats.netProfit > 0)
    assertEquals(50, stats.totalBattles)

    val camps = BattleAnalytics.groupCamps(battles)
    assertTrue("Should group into distinct camps", camps.isNotEmpty())
  }

  @Test
  fun `outpost sieges and google sheets tsv exporter work correctly`() {
    val sieges = OutpostData.sampleSieges
    assertTrue("Outpost sieges should contain battles", sieges.isNotEmpty())
    assertEquals(5, sieges.size)

    val tverdinyaSiege = sieges.first { it.outpostName == "Твердыня" }
    assertEquals("Экономический", tverdinyaSiege.outpostType)
    assertEquals(2, tverdinyaSiege.participatingClansCount)
    assertEquals("Сумеречный патруль", tverdinyaSiege.winnerClan?.clanName)

    val fortSiege = sieges.first { it.outpostName == "Форт" }
    assertEquals("Форт", fortSiege.outpostName)
    assertEquals("Логистика", fortSiege.outpostType)
    assertEquals(2, fortSiege.participatingClansCount)
    assertEquals("Crypto Wolf", fortSiege.winnerClan?.clanName)

    val tsv = GoogleSheetsExporter.generateSiegesTsv(sieges)
    assertTrue("TSV should contain header", tsv.contains("Дата и время"))
    assertTrue("TSV should contain Crypto Wolf", tsv.contains("Crypto Wolf"))

    val participantsTsv = GoogleSheetsExporter.generateParticipantsTsv(sieges)
    assertTrue("Participants TSV should contain player names", participantsTsv.contains("Аль Чаг"))
    assertTrue("Participants TSV should contain AlphaWolf_77", participantsTsv.contains("AlphaWolf_77"))
    assertTrue("Participants TSV should contain Вклад column", participantsTsv.contains("Вклад (мощь ⚔️)"))

    val compact3ColTsv = GoogleSheetsExporter.generateCompact3ColTsv(sieges)
    assertTrue("Compact 3-col should have exact header", compact3ColTsv.startsWith("Дата\tНазвание аванпоста\tВклад\n"))
    assertTrue("Compact 3-col should contain player and power formatted", compact3ColTsv.contains("Аль Чаг 35.8M") || compact3ColTsv.contains("Аль Чаг"))

    val standard4ColTsv = GoogleSheetsExporter.generateStandard4ColTsv(sieges)
    assertTrue("Standard 4-col should have exact header", standard4ColTsv.startsWith("Дата\tНазвание аванпоста\tУчастник\tВклад\n"))
    assertTrue("Standard 4-col should contain separate player column", standard4ColTsv.contains("Аль Чаг\t"))
  }
}
