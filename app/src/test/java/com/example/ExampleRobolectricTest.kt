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
    assertEquals(4, sieges.size)

    val fortSiege = sieges.first()
    assertEquals("Форт", fortSiege.outpostName)
    assertEquals("Логистика", fortSiege.outpostType)
    assertEquals(2, fortSiege.participatingClansCount)
    assertEquals("Crypto Wolf", fortSiege.winnerClan?.clanName)

    val tsv = GoogleSheetsExporter.generateSiegesTsv(sieges)
    assertTrue("TSV should contain header", tsv.contains("Дата и время"))
    assertTrue("TSV should contain Crypto Wolf", tsv.contains("Crypto Wolf"))

    val participantsTsv = GoogleSheetsExporter.generateParticipantsTsv(sieges)
    assertTrue("Participants TSV should contain player names", participantsTsv.contains("AlphaWolf_77"))
  }
}
