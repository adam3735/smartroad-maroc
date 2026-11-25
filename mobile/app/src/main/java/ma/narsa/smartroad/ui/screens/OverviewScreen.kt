package ma.narsa.smartroad.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.data.NarsaDataEngine
import ma.narsa.smartroad.ui.components.ChartSeriesPoint
import ma.narsa.smartroad.ui.components.FloatingRouteFilterCard
import ma.narsa.smartroad.ui.components.HighwayAutopilotCard
import ma.narsa.smartroad.ui.components.InteractiveGroupedChart
import ma.narsa.smartroad.ui.components.InteractiveSplineChart
import ma.narsa.smartroad.ui.components.OncfCurvedHeader
import ma.narsa.smartroad.ui.components.PerforatedTicketCard
import ma.narsa.smartroad.ui.components.SpeedometerArcGauge
import ma.narsa.smartroad.ui.components.formatNumberFr
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverviewScreen(
    isWide: Boolean,
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    var selectedZone by remember { mutableStateOf("Total") }
    var selectedYear by remember { mutableIntStateOf(2024) }

    val currentRecord = remember(selectedZone, selectedYear) {
        NarsaDataEngine.allRecords.first { it.zone == selectedZone && it.annee == selectedYear }
    }
    val kpis = NarsaDataEngine.kpis
    val years = listOf(2021, 2022, 2023, 2024)

    val zoneShort = when (selectedZone) {
        "Agglomeration" -> strings.zoneUrban
        "Hors_agglomeration" -> strings.zoneRural
        else -> strings.zoneNational
    }

    val accidentSeries = years.map { yr ->
        val agg = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Agglomeration" }
        val hors = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Hors_agglomeration" }
        ChartSeriesPoint(yr.toString(), agg.totalAccidents.toFloat(), hors.totalAccidents.toFloat())
    }
    val deathsSeries = years.map { yr ->
        val agg = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Agglomeration" }
        val hors = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Hors_agglomeration" }
        ChartSeriesPoint(yr.toString(), hors.tues.toFloat(), agg.tues.toFloat())
    }

    val pagerState = rememberPagerState(pageCount = { 3 })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                OncfCurvedHeader(
                    title = strings.overviewTitle,
                    subtitle = strings.overviewSubtitle,
                    isNight = isNight,
                    onToggleNight = onToggleNight,
                    onToggleLanguage = onToggleLanguage,
                    extraBottomPadding = 44.dp
                )
                FloatingRouteFilterCard(
                    selectedZone = selectedZone,
                    selectedYear = selectedYear,
                    onZoneChange = { selectedZone = it },
                    onYearChange = { selectedYear = it },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-24).dp)
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    pageSpacing = 12.dp
                ) { page ->
                    when (page) {
                        0 -> PerforatedTicketCard(
                            headerTag = "${strings.ticketBilan} $selectedYear • $zoneShort",
                            accentColor = palette.solarOrange,
                            stubLeft = "${strings.cumul4YearsAcc} : ${formatNumberFr(kpis.cumulAccidents4Ans)}",
                            stubRight = "${currentRecord.victimesParAccident} ${strings.victPerAccTag}"
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(strings.accidentsCorp, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                    Text(formatNumberFr(currentRecord.totalAccidents), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = palette.inkPrimary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(strings.totalVictims, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                    Text(formatNumberFr(currentRecord.totalVictimes), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = palette.skyCyan)
                                }
                            }
                        }
                        1 -> PerforatedTicketCard(
                            headerTag = "${strings.ticketLethality} $selectedYear",
                            accentColor = palette.coralAlert,
                            stubLeft = "${strings.cumul4YearsDeaths} : ${formatNumberFr(kpis.cumulTues4Ans)}",
                            stubRight = "${strings.mortalityTag} ${currentRecord.tauxMortalitePct}%"
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(strings.deathsTues, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                    Text(formatNumberFr(currentRecord.tues), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = palette.coralAlert)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(strings.seriousInjuries, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                    Text(formatNumberFr(currentRecord.blessesGraves), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = palette.solarOrange)
                                }
                            }
                        }
                        else -> PerforatedTicketCard(
                            headerTag = strings.ticketSeverity,
                            accentColor = palette.skyCyan,
                            stubLeft = strings.urbanAccShare,
                            stubRight = strings.ruralDeathShare
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(strings.severityIndex, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                    Text("${currentRecord.indiceGravitePct} %", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = palette.inkPrimary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(strings.lightInjuries, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                    Text(formatNumberFr(currentRecord.blessesLegers), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = palette.inkSecondary)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { idx ->
                        val active = pagerState.currentPage == idx
                        val w by animateDpAsState(if (active) 24.dp else 7.dp, label = "pill_w")
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .height(6.dp)
                                .width(w)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (active) palette.solarOrange else palette.borderStrong)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpeedometerArcGauge(
                    title = strings.gaugeMortality,
                    valueText = "${currentRecord.tauxMortalitePct}%",
                    subtitle = "$selectedYear • $zoneShort",
                    fraction = (currentRecord.tauxMortalitePct / 12.0).toFloat(),
                    arcColor = palette.coralAlert,
                    modifier = Modifier.weight(1f)
                )
                SpeedometerArcGauge(
                    title = strings.gaugeSeverity,
                    valueText = "${currentRecord.indiceGravitePct}%",
                    subtitle = strings.deathsPlusSerious,
                    fraction = (currentRecord.indiceGravitePct / 18.0).toFloat(),
                    arcColor = palette.solarOrange,
                    modifier = Modifier.weight(1f)
                )
                SpeedometerArcGauge(
                    title = strings.gaugeSerious,
                    valueText = "${currentRecord.tauxBlessesGravesPct}%",
                    subtitle = strings.shareOfAcc,
                    fraction = (currentRecord.tauxBlessesGravesPct / 18.0).toFloat(),
                    arcColor = palette.skyCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            HighwayAutopilotCard(modifier = Modifier.padding(horizontal = 16.dp))
        }

        item {
            val chartModifier = Modifier.padding(horizontal = 16.dp)
            if (isWide) {
                Row(modifier = chartModifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ChartCard(title = strings.chartVolumeTitle, modifier = Modifier.weight(1f)) {
                        InteractiveGroupedChart(accidentSeries, strings.labelAgglo, palette.skyCyan, strings.labelRural, palette.solarOrange)
                    }
                    ChartCard(title = strings.chartDeathsTitle, modifier = Modifier.weight(1f)) {
                        InteractiveSplineChart(deathsSeries, strings.labelDeathsRural, palette.solarOrange, strings.labelDeathsAgglo, palette.skyCyan)
                    }
                }
            } else {
                Column(modifier = chartModifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ChartCard(title = strings.chartVolumeTitle) {
                        InteractiveGroupedChart(accidentSeries, strings.labelAgglo, palette.skyCyan, strings.labelRural, palette.solarOrange)
                    }
                    ChartCard(title = strings.chartDeathsTitle) {
                        InteractiveSplineChart(deathsSeries, strings.labelDeathsRural, palette.solarOrange, strings.labelDeathsAgglo, palette.skyCyan)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigate("comparaison") },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.solarOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text(strings.btnCockpit, fontWeight = FontWeight.Bold, maxLines = 1) }

                OutlinedButton(
                    onClick = { onNavigate("ia") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text(strings.btnAiSim, color = palette.inkPrimary, fontWeight = FontWeight.Bold, maxLines = 1) }
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val palette = LocalOncfPalette.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
            content()
        }
    }
}
