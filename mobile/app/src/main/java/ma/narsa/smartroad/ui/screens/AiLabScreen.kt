package ma.narsa.smartroad.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.data.NarsaDataEngine
import ma.narsa.smartroad.data.RiskClassificationResult
import ma.narsa.smartroad.data.TrendPredictionResult
import ma.narsa.smartroad.ui.components.ChartSeriesPoint
import ma.narsa.smartroad.ui.components.InteractiveSplineChart
import ma.narsa.smartroad.ui.components.OncfCurvedHeader
import ma.narsa.smartroad.ui.components.OncfSegmentedBar
import ma.narsa.smartroad.ui.components.PerforatedTicketCard
import ma.narsa.smartroad.ui.components.formatNumberFr
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun AiLabScreen(
    isWide: Boolean,
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val haptics = LocalHapticFeedback.current

    var draftAnnee by remember { mutableIntStateOf(2026) }
    var draftZone by remember { mutableStateOf("Agglomeration") }
    var committedTrend by remember { mutableStateOf<TrendPredictionResult?>(null) }

    var draftMort by remember { mutableDoubleStateOf(8.1) }
    var draftGraves by remember { mutableDoubleStateOf(13.2) }
    var draftVict by remember { mutableDoubleStateOf(1.75) }
    var committedRisk by remember { mutableStateOf<RiskClassificationResult?>(null) }

    var benchTab by remember { mutableStateOf("reg") }

    val trendDirty = committedTrend != null &&
        (committedTrend!!.annee != draftAnnee || committedTrend!!.zone != draftZone)

    val riskDirty = committedRisk != null &&
        (abs(committedRisk!!.tauxMortaliteSaisi - draftMort) > 0.01 ||
            abs(committedRisk!!.tauxBlessesGravesSaisi - draftGraves) > 0.01 ||
            abs(committedRisk!!.victimesParAccSaisi - draftVict) > 0.01)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OncfCurvedHeader(
                title = strings.aiTitle,
                subtitle = strings.aiSubtitle,
                isNight = isNight,
                onToggleNight = onToggleNight,
                onToggleLanguage = onToggleLanguage
            )
        }

        item {
            val trendCard: @Composable (Modifier) -> Unit = { mod ->
                Card(
                    modifier = mod.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(strings.sim1Title, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.targetYear, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = palette.inkSecondary)
                            Text("$draftAnnee", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = palette.solarOrange)
                        }
                        Slider(
                            value = draftAnnee.toFloat(),
                            onValueChange = { draftAnnee = it.roundToInt() },
                            valueRange = 2025f..2035f,
                            steps = 9,
                            colors = SliderDefaults.colors(thumbColor = palette.solarOrange, activeTrackColor = palette.solarOrange)
                        )

                        OncfSegmentedBar(
                            options = listOf("Agglomeration" to strings.zoneUrban, "Hors_agglomeration" to strings.zoneRural),
                            selected = draftZone,
                            onSelect = { draftZone = it }
                        )

                        Button(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                committedTrend = NarsaDataEngine.predictTrend(draftAnnee, draftZone)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = palette.solarOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) { Text(strings.btnGenProjection, fontWeight = FontWeight.Bold) }

                        if (trendDirty) {
                            Text(
                                strings.dirtyParamsMsg,
                                fontSize = 12.sp,
                                color = palette.amberSignal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        AnimatedVisibility(visible = committedTrend != null) {
                            committedTrend?.let { res ->
                                val zLbl = if (res.zone == "Agglomeration") strings.zoneUrban else strings.zoneRural
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    PerforatedTicketCard(
                                        headerTag = "${strings.projectionTag} ${res.annee} • $zLbl",
                                        accentColor = palette.solarOrange,
                                        stubLeft = "${strings.maePrefix} : ± ${formatNumberFr(res.maeMargin)}",
                                        stubRight = "${if (res.slopePerYear >= 0) "+" else ""}${formatNumberFr(res.slopePerYear)} ${strings.perYearSuffix}"
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text(strings.estAccidents, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                                Text(formatNumberFr(res.accidentsEstimes), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = palette.solarOrange)
                                            }
                                            Column {
                                                Text(strings.deltaVs2024, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                                                Text("${if (res.deltaVs2024 >= 0) "+" else ""}${res.pctVs2024}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = palette.skyCyan)
                                            }
                                        }
                                    }

                                    val pts = res.trajectory2021ToTarget.map { (yr, v) ->
                                        ChartSeriesPoint(yr.toString(), v.toFloat(), res.reference2024.toFloat())
                                    }
                                    InteractiveSplineChart(
                                        data = pts,
                                        labelA = "${strings.trajPrefix}${res.annee}",
                                        colorA = palette.solarOrange,
                                        labelB = strings.ref2024Label,
                                        colorB = palette.skyCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val riskCard: @Composable (Modifier) -> Unit = { mod ->
                Card(
                    modifier = mod.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(strings.sim2Title, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { draftMort = 1.7; draftGraves = 8.9; draftVict = 1.41 },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) { Text(strings.presetUrbanEx, fontSize = 11.5.sp, color = palette.inkPrimary, maxLines = 1) }
                            OutlinedButton(
                                onClick = { draftMort = 8.2; draftGraves = 12.9; draftVict = 1.75 },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) { Text(strings.presetRuralEx, fontSize = 11.5.sp, color = palette.inkPrimary, maxLines = 1) }
                        }

                        AiSliderRow(strings.mortalityRateInput, draftMort, 0.5f..15f) { draftMort = it }
                        AiSliderRow(strings.seriousRateInput, draftGraves, 5f..20f) { draftGraves = it }
                        AiSliderRow(strings.victPerAccInput, draftVict, 1.0f..3.0f) { draftVict = it }

                        Button(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                committedRisk = NarsaDataEngine.classifyRisk(draftMort, draftGraves, draftVict)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = palette.navyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) { Text(strings.btnDiagnose, color = Color.White, fontWeight = FontWeight.Bold) }

                        if (riskDirty) {
                            Text(
                                strings.dirtyDiagMsg,
                                fontSize = 12.sp,
                                color = palette.amberSignal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        AnimatedVisibility(visible = committedRisk != null) {
                            committedRisk?.let { res ->
                                val isHigh = res.profilPredit == "Hors_agglomeration"
                                val pAggAnim by animateFloatAsState(res.probabiliteAgglomeration.toFloat(), tween(450), label = "pAgg")
                                val pHorsAnim by animateFloatAsState(res.probabiliteHorsAgglomeration.toFloat(), tween(450), label = "pHors")
                                PerforatedTicketCard(
                                    headerTag = strings.diagTag,
                                    accentColor = if (isHigh) palette.solarOrange else palette.skyCyan,
                                    stubLeft = if (isHigh) strings.riskHighLabel else strings.riskModLabel,
                                    stubRight = if (isHigh) strings.ruralBadge else strings.urbanBadge
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        AiProbBar(strings.profUrban, pAggAnim, palette.skyCyan)
                                        AiProbBar(strings.profRural, pHorsAnim, palette.solarOrange)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val containerMod = Modifier.padding(horizontal = 16.dp)
            if (isWide) {
                Row(modifier = containerMod.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    trendCard(Modifier.weight(1f))
                    riskCard(Modifier.weight(1f))
                }
            } else {
                Column(modifier = containerMod, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    trendCard(Modifier)
                    riskCard(Modifier)
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.benchTitle, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
                    OncfSegmentedBar(
                        options = listOf("reg" to strings.benchRegTab, "clf" to strings.benchClfTab),
                        selected = benchTab,
                        onSelect = { benchTab = it }
                    )
                    if (benchTab == "reg") {
                        NarsaDataEngine.regressionBenchmarks.forEachIndexed { idx, b ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(b.modele, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = palette.inkPrimary)
                                Text("MAE ${b.mae} • RMSE ${b.rmse}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = palette.inkSecondary)
                            }
                            if (idx < NarsaDataEngine.regressionBenchmarks.lastIndex) HorizontalDivider(color = palette.border.copy(alpha = 0.55f))
                        }
                    } else {
                        NarsaDataEngine.classificationBenchmarks.forEachIndexed { idx, b ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(b.modele, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = palette.inkPrimary)
                                Text("Acc ${(b.accuracy * 100).toInt()}% • F1 ${b.f1Score}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = palette.skyCyan, fontWeight = FontWeight.Bold)
                            }
                            if (idx < NarsaDataEngine.classificationBenchmarks.lastIndex) HorizontalDivider(color = palette.border.copy(alpha = 0.55f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiSliderRow(
    label: String,
    value: Double,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Double) -> Unit
) {
    val palette = LocalOncfPalette.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = palette.inkSecondary)
        Text(String.format(Locale.US, "%.2f", value), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = palette.inkPrimary)
    }
    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange((it * 100).roundToInt() / 100.0) },
        valueRange = range,
        colors = SliderDefaults.colors(thumbColor = palette.skyCyan, activeTrackColor = palette.skyCyan)
    )
}

@Composable
private fun AiProbBar(label: String, prob: Float, color: Color) {
    val palette = LocalOncfPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, color = palette.inkSecondary)
            Text(String.format(Locale.US, "%.1f%%", prob * 100f), fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.inkPrimary)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(palette.surfaceMuted)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(prob.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(color)
            )
        }
    }
}
