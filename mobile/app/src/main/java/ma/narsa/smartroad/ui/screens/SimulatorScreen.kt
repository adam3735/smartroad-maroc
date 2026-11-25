package ma.narsa.smartroad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import ma.narsa.smartroad.data.ZoneComparison
import ma.narsa.smartroad.ui.components.BrakingCrashCockpit
import ma.narsa.smartroad.ui.components.OncfCurvedHeader
import ma.narsa.smartroad.ui.components.PerforatedTicketCard
import ma.narsa.smartroad.ui.components.formatNumberFr
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette

@Composable
fun SimulatorScreen(
    isWide: Boolean,
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val zones = NarsaDataEngine.zoneComparisons

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OncfCurvedHeader(
                title = strings.simTitle,
                subtitle = strings.simSubtitle,
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.simCardTitle, style = MaterialTheme.typography.titleMedium, color = palette.inkPrimary)
                    BrakingCrashCockpit()
                }
            }
        }

        item {
            val mod = Modifier.padding(horizontal = 16.dp)
            if (isWide) {
                Row(modifier = mod.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    zones.forEach { z -> ZoneTicketCard(z, Modifier.weight(1f)) }
                }
            } else {
                Column(modifier = mod, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    zones.forEach { z -> ZoneTicketCard(z) }
                }
            }
        }
    }
}

@Composable
private fun ZoneTicketCard(z: ZoneComparison, modifier: Modifier = Modifier) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val isAgglo = z.zone == "Agglomeration"
    PerforatedTicketCard(
        headerTag = if (isAgglo) strings.urbanHeader else strings.ruralHeader,
        accentColor = if (isAgglo) palette.skyCyan else palette.solarOrange,
        stubLeft = if (isAgglo) strings.urbanStub else strings.ruralStub,
        stubRight = "${z.tauxMortaliteMoyenPct}% ${strings.mortalityStub}",
        modifier = modifier
    ) {
        val rows = listOf(
            strings.rowCumulAcc to formatNumberFr(z.totalAccidents),
            strings.rowCumulDeaths to formatNumberFr(z.totalTues),
            strings.rowCumulSerious to formatNumberFr(z.totalBlessesGraves),
            strings.rowCumulVictims to formatNumberFr(z.totalVictimes),
            strings.rowAvgSerious to "${z.tauxBlessesGravesMoyenPct} %",
            strings.rowAvgVictPerAcc to "${z.victimesParAccidentMoyen}"
        )
        rows.forEachIndexed { idx, (k, v) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(k, style = MaterialTheme.typography.bodyMedium, color = palette.inkSecondary)
                Text(v, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = palette.inkPrimary)
            }
            if (idx < rows.lastIndex) HorizontalDivider(color = palette.border.copy(alpha = 0.55f))
        }
    }
}
