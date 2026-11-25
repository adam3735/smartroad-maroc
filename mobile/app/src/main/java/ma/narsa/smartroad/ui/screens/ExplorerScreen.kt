package ma.narsa.smartroad.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ma.narsa.smartroad.data.NarsaDataEngine
import ma.narsa.smartroad.ui.components.ChartSeriesPoint
import ma.narsa.smartroad.ui.components.InteractiveGroupedChart
import ma.narsa.smartroad.ui.components.InteractiveSplineChart
import ma.narsa.smartroad.ui.components.OncfCurvedHeader
import ma.narsa.smartroad.ui.components.OncfSegmentedBar
import ma.narsa.smartroad.ui.components.PerforatedTicketCard
import ma.narsa.smartroad.ui.components.formatNumberFr
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette

@Composable
fun ExplorerScreen(
    isWide: Boolean,
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var filtreZone by remember { mutableStateOf("Total") }
    var filtreAnnee by remember { mutableStateOf("Toutes") }

    val filtered = remember(filtreZone, filtreAnnee) {
        NarsaDataEngine.allRecords.filter { r ->
            val okZone = filtreZone == "Toutes" || r.zone == filtreZone
            val okAnnee = filtreAnnee == "Toutes" || r.annee.toString() == filtreAnnee
            okZone && okAnnee
        }
    }

    val sumAccidents = filtered.sumOf { it.totalAccidents }
    val sumTues = filtered.sumOf { it.tues }
    val sumGraves = filtered.sumOf { it.blessesGraves }
    val sumVictimes = filtered.sumOf { it.totalVictimes }

    val barPoints = filtered.map {
        ChartSeriesPoint(
            label = if (filtreZone == "Toutes") "${it.annee}-${it.zone.take(3)}" else it.annee.toString(),
            valueA = it.totalAccidents.toFloat(),
            valueB = it.totalVictimes.toFloat()
        )
    }
    val splinePoints = filtered.map {
        ChartSeriesPoint(
            label = if (filtreZone == "Toutes") "${it.annee}-${it.zone.take(3)}" else it.annee.toString(),
            valueA = it.blessesGraves.toFloat(),
            valueB = it.tues.toFloat()
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OncfCurvedHeader(
                title = strings.explorerTitle,
                subtitle = strings.explorerSubtitle,
                isNight = isNight,
                onToggleNight = onToggleNight,
                onToggleLanguage = onToggleLanguage
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface),
                border = BorderStroke(1.dp, palette.border)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(strings.filterEnv, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = palette.inkMuted)
                    OncfSegmentedBar(
                        options = listOf("Total" to strings.zoneNational, "Agglomeration" to strings.zoneUrban, "Hors_agglomeration" to strings.zoneRural, "Toutes" to strings.zoneAll),
                        selected = filtreZone,
                        onSelect = { filtreZone = it }
                    )

                    Text(strings.filterPeriod, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = palette.inkMuted)
                    OncfSegmentedBar(
                        options = listOf("Toutes" to "2021–24", "2021" to "2021", "2022" to "2022", "2023" to "2023", "2024" to "2024"),
                        selected = filtreAnnee,
                        onSelect = { filtreAnnee = it }
                    )

                    Button(
                        onClick = {
                            val csv = NarsaDataEngine.buildCsv(filtered)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_SUBJECT, "SmartRoad Maroc NARSA ($filtreZone - $filtreAnnee)")
                                putExtra(Intent.EXTRA_TEXT, csv)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, strings.exportCsvBtn))
                            scope.launch {
                                snackbarHostState.showSnackbar("${strings.exportCsvToast} (${filtered.size})")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = palette.solarOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(strings.exportCsvBtn, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            PerforatedTicketCard(
                headerTag = "${strings.selectionConsolidation} ($filtreZone • $filtreAnnee)",
                accentColor = palette.skyCyan,
                stubLeft = "${filtered.size} ${strings.filteredRowsLabel}",
                stubRight = "${formatNumberFr(sumVictimes)} ${strings.labelVictims}",
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(strings.colAccidents, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                        Text(formatNumberFr(sumAccidents), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = palette.inkPrimary)
                    }
                    Column {
                        Text(strings.colDeaths, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                        Text(formatNumberFr(sumTues), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = palette.coralAlert)
                    }
                    Column {
                        Text(strings.colSerious, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                        Text(formatNumberFr(sumGraves), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = palette.solarOrange)
                    }
                }
            }
        }

        item {
            val mod = Modifier.padding(horizontal = 16.dp)
            if (isWide) {
                Row(modifier = mod.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ExplorerChartCard(strings.chartAccVsVict, Modifier.weight(1f)) {
                        InteractiveGroupedChart(barPoints, strings.labelAccidents, palette.skyCyan, strings.labelVictims, palette.solarOrange)
                    }
                    ExplorerChartCard(strings.chartSeriousVsDeaths, Modifier.weight(1f)) {
                        InteractiveSplineChart(splinePoints, strings.labelSerious, palette.skyCyan, strings.labelDeaths, palette.coralAlert)
                    }
                }
            } else {
                Column(modifier = mod, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ExplorerChartCard(strings.chartAccVsVict) {
                        InteractiveGroupedChart(barPoints, strings.labelAccidents, palette.skyCyan, strings.labelVictims, palette.solarOrange)
                    }
                    ExplorerChartCard(strings.chartSeriousVsDeaths) {
                        InteractiveSplineChart(splinePoints, strings.labelSerious, palette.skyCyan, strings.labelDeaths, palette.coralAlert)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface),
                border = BorderStroke(1.dp, palette.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(strings.officialLedgerTitle, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
                    Spacer(Modifier.height(10.dp))
                    Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LedgerCell(strings.colYear, 56, true)
                            LedgerCell(strings.colZone, 140, true)
                            LedgerCell(strings.colAccidents, 88, true)
                            LedgerCell(strings.colDeaths, 68, true)
                            LedgerCell(strings.colSerious, 84, true)
                            LedgerCell(strings.colVictims, 88, true)
                            LedgerCell(strings.colMortality, 76, true)
                            LedgerCell(strings.colSeverity, 76, true)
                        }
                        HorizontalDivider(color = palette.border)
                        filtered.forEach { r ->
                            Row(modifier = Modifier.padding(vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                LedgerCell("${r.annee}", 56, true)
                                LedgerCell(r.zone, 140)
                                LedgerCell(formatNumberFr(r.totalAccidents), 88)
                                LedgerCell(formatNumberFr(r.tues), 68, alert = true)
                                LedgerCell(formatNumberFr(r.blessesGraves), 84)
                                LedgerCell(formatNumberFr(r.totalVictimes), 88)
                                LedgerCell("${r.tauxMortalitePct}%", 76, alert = true)
                                LedgerCell("${r.indiceGravitePct}%", 76)
                            }
                            HorizontalDivider(color = palette.border.copy(alpha = 0.55f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplorerChartCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val palette = LocalOncfPalette.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
            content()
        }
    }
}

@Composable
private fun LedgerCell(
    text: String,
    widthDp: Int,
    isHeader: Boolean = false,
    alert: Boolean = false
) {
    val palette = LocalOncfPalette.current
    Text(
        text = text,
        modifier = Modifier.width(widthDp.dp),
        fontFamily = if (isHeader) FontFamily.SansSerif else FontFamily.Monospace,
        fontWeight = if (isHeader || alert) FontWeight.Bold else FontWeight.Normal,
        fontSize = if (isHeader) 12.sp else 12.5.sp,
        color = when {
            alert -> palette.coralAlert
            isHeader -> palette.inkMuted
            else -> palette.inkPrimary
        }
    )
}
