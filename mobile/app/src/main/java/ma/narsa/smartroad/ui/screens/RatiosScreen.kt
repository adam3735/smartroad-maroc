package ma.narsa.smartroad.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.data.NarsaDataEngine
import ma.narsa.smartroad.ui.components.ChartSeriesPoint
import ma.narsa.smartroad.ui.components.InteractiveSplineChart
import ma.narsa.smartroad.ui.components.OncfCurvedHeader
import ma.narsa.smartroad.ui.components.SpeedometerArcGauge
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette
import java.util.Locale

@Composable
fun RatiosScreen(
    isWide: Boolean,
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val years = listOf(2021, 2022, 2023, 2024)
    val mortalitySeries = years.map { yr ->
        val agg = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Agglomeration" }
        val hors = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Hors_agglomeration" }
        ChartSeriesPoint(yr.toString(), hors.tauxMortalitePct.toFloat(), agg.tauxMortalitePct.toFloat())
    }
    val severeSeries = years.map { yr ->
        val agg = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Agglomeration" }
        val hors = NarsaDataEngine.allRecords.first { it.annee == yr && it.zone == "Hors_agglomeration" }
        ChartSeriesPoint(yr.toString(), hors.tauxBlessesGravesPct.toFloat(), agg.tauxBlessesGravesPct.toFloat())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OncfCurvedHeader(
                title = strings.ratiosTitle,
                subtitle = strings.ratiosSubtitle,
                isNight = isNight,
                onToggleNight = onToggleNight,
                onToggleLanguage = onToggleLanguage
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpeedometerArcGauge(strings.ratioNatMort, "2.81%", strings.dropVs2021, 0.28f, palette.coralAlert, Modifier.weight(1f))
                SpeedometerArcGauge(strings.ratioNatSev, "8.88%", strings.bilan2024, 0.44f, palette.solarOrange, Modifier.weight(1f))
                SpeedometerArcGauge(strings.ratioNatVict, "1.47", strings.avg2024, 0.59f, palette.skyCyan, Modifier.weight(1f))
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
                    val formulas = listOf(
                        Triple(strings.formulaMortTitle, "(Tués / Total Accidents) × 100", strings.formulaMortDesc),
                        Triple(strings.formulaSevTitle, "((Tués + Blessés Graves) / Victimes) × 100", strings.formulaSevDesc),
                        Triple(strings.formulaVictTitle, "Total Victimes / Total Accidents", strings.formulaVictDesc)
                    )
                    formulas.forEachIndexed { idx, (title, formula, desc) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
                            Text(formula, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = palette.solarOrange, fontWeight = FontWeight.Bold)
                            Text(desc, style = MaterialTheme.typography.bodyMedium, color = palette.inkSecondary)
                        }
                        if (idx < formulas.lastIndex) HorizontalDivider(color = palette.border, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }

        item {
            val fmtPct: (Float) -> String = { String.format(Locale.US, "%.1f%%", it) }
            val mod = Modifier.padding(horizontal = 16.dp)
            if (isWide) {
                Row(modifier = mod.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    RatioChartCard(strings.chartMortPct, Modifier.weight(1f)) {
                        InteractiveSplineChart(mortalitySeries, strings.labelRural, palette.solarOrange, strings.labelAgglo, palette.skyCyan, fmtPct)
                    }
                    RatioChartCard(strings.chartSevPct, Modifier.weight(1f)) {
                        InteractiveSplineChart(severeSeries, strings.labelRural, palette.solarOrange, strings.labelAgglo, palette.skyCyan, fmtPct)
                    }
                }
            } else {
                Column(modifier = mod, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    RatioChartCard(strings.chartMortPct) {
                        InteractiveSplineChart(mortalitySeries, strings.labelRural, palette.solarOrange, strings.labelAgglo, palette.skyCyan, fmtPct)
                    }
                    RatioChartCard(strings.chartSevPct) {
                        InteractiveSplineChart(severeSeries, strings.labelRural, palette.solarOrange, strings.labelAgglo, palette.skyCyan, fmtPct)
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
                    Text(strings.matrixTitle, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
                    Spacer(Modifier.height(10.dp))
                    Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(strings.colYear, Modifier.width(56.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.inkMuted)
                            Text(strings.colZone, Modifier.width(140.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.inkMuted)
                            Text(strings.colMortality, Modifier.width(76.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.inkMuted)
                            Text(strings.colSerious, Modifier.width(76.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.inkMuted)
                            Text(strings.colSeverity, Modifier.width(76.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.inkMuted)
                            Text(strings.victPerAccTag, Modifier.width(76.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.inkMuted)
                        }
                        HorizontalDivider(color = palette.border)
                        NarsaDataEngine.allRecords.forEach { r ->
                            Row(modifier = Modifier.padding(vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("${r.annee}", Modifier.width(56.dp), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = palette.inkPrimary)
                                Text(r.zone, Modifier.width(140.dp), fontSize = 12.5.sp, color = palette.inkPrimary)
                                Text("${r.tauxMortalitePct}%", Modifier.width(76.dp), fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, color = palette.solarOrange, fontWeight = FontWeight.Bold)
                                Text("${r.tauxBlessesGravesPct}%", Modifier.width(76.dp), fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, color = palette.inkPrimary)
                                Text("${r.indiceGravitePct}%", Modifier.width(76.dp), fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, color = palette.inkPrimary)
                                Text("${r.victimesParAccident}", Modifier.width(76.dp), fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, color = palette.inkPrimary)
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
private fun RatioChartCard(
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
