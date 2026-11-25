package ma.narsa.smartroad.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette
import kotlin.math.abs
import kotlin.math.max

private enum class AutoVehicleKind { SEDAN, SUV, COACH, MOTO }
private enum class TurnSignal { NONE, LEFT, RIGHT }

private class HighwayUnit(
    val id: Int,
    val kind: AutoVehicleKind,
    var x: Float,
    var lane: Int,
    var targetLane: Int,
    var lateralY: Float,
    var speed: Float,
    val desiredSpeed: Float,
    val length: Float,
    val width: Float,
    val bodyColor: Color,
    var signal: TurnSignal = TurnSignal.NONE,
    var signalPreTimer: Float = 0f,
    var isBraking: Boolean = false
)

@Composable
fun HighwayAutopilotCard(modifier: Modifier = Modifier) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    var isDense by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var frameTick by remember { mutableIntStateOf(0) }
    var roadWidthPx by remember { mutableStateOf(960f) }

    val units = remember { mutableStateListOf<HighwayUnit>() }

    LaunchedEffect(isDense) {
        units.clear()
        val list = if (isDense) {
            listOf(
                HighwayUnit(1, AutoVehicleKind.COACH, 140f, 2, 2, 2f, 44f, 46f, 134f, 34f, Color(0xFFE2E8F0)),
                HighwayUnit(2, AutoVehicleKind.SEDAN, 360f, 2, 2, 2f, 64f, 70f, 82f, 28f, Color(0xFF38BDF8)),
                HighwayUnit(3, AutoVehicleKind.SUV, 650f, 2, 2, 2f, 58f, 60f, 92f, 31f, Color(0xFF94A3B8)),
                HighwayUnit(4, AutoVehicleKind.SEDAN, 200f, 1, 1, 1f, 68f, 72f, 82f, 28f, Color(0xFFFF6B00)),
                HighwayUnit(5, AutoVehicleKind.MOTO, 480f, 1, 1, 1f, 74f, 76f, 48f, 18f, Color(0xFF38BDF8)),
                HighwayUnit(6, AutoVehicleKind.SUV, 780f, 1, 1, 1f, 60f, 62f, 92f, 31f, Color(0xFF6366F1)),
                HighwayUnit(7, AutoVehicleKind.SEDAN, 110f, 0, 0, 0f, 72f, 74f, 82f, 28f, Color(0xFFF8FAFC)),
                HighwayUnit(8, AutoVehicleKind.MOTO, 580f, 0, 0, 0f, 76f, 78f, 48f, 18f, Color(0xFFFF6B00))
            )
        } else {
            listOf(
                HighwayUnit(1, AutoVehicleKind.COACH, 200f, 2, 2, 2f, 46f, 46f, 134f, 34f, Color(0xFFE2E8F0)),
                HighwayUnit(2, AutoVehicleKind.SEDAN, 30f, 2, 2, 2f, 66f, 72f, 82f, 28f, Color(0xFFFF6B00)),
                HighwayUnit(3, AutoVehicleKind.SUV, 430f, 1, 1, 1f, 60f, 60f, 92f, 31f, Color(0xFF6366F1)),
                HighwayUnit(4, AutoVehicleKind.MOTO, 210f, 1, 1, 1f, 74f, 76f, 48f, 18f, Color(0xFF38BDF8)),
                HighwayUnit(5, AutoVehicleKind.SEDAN, 640f, 0, 0, 0f, 72f, 74f, 82f, 28f, Color(0xFFF8FAFC))
            )
        }
        units.addAll(list)
    }

    LaunchedEffect(isPaused, isDense) {
        if (isPaused) return@LaunchedEffect
        var lastNanos = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastNanos == 0L) {
                    lastNanos = now
                    return@withFrameNanos
                }
                val dt = ((now - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = now

                val w = roadWidthPx.coerceAtLeast(600f)

                fun isLaneClear(self: HighwayUnit, candLane: Int): Boolean {
                    if (candLane !in 0..2) return false
                    for (other in units) {
                        if (other.id == self.id) continue
                        val occupies = other.lane == candLane ||
                            other.targetLane == candLane ||
                            abs(other.lateralY - candLane) < 0.72f
                        if (!occupies) continue

                        val dx = other.x - self.x
                        val minAhead = (self.length + other.length) * 0.65f + 46f
                        val minBehind = (self.length + other.length) * 0.75f + 58f +
                            max(0f, other.speed - self.speed) * 1.2f
                        if (dx >= 0f && dx < minAhead) return false
                        if (dx < 0f && -dx < minBehind) return false
                    }
                    return true
                }

                for (self in units) {
                    var leader: HighwayUnit? = null
                    var minGap = Float.MAX_VALUE
                    for (other in units) {
                        if (other.id == self.id) continue
                        val samePath = other.lane == self.lane ||
                            other.targetLane == self.lane ||
                            other.lane == self.targetLane ||
                            abs(other.lateralY - self.lateralY) < 0.62f
                        if (!samePath) continue
                        val gap = (other.x - other.length / 2f) - (self.x + self.length / 2f)
                        if (gap > -10f && gap < minGap) {
                            minGap = gap
                            leader = other
                        }
                    }

                    val safeFollowGap = self.speed * 0.95f + 36f

                    if (leader != null &&
                        minGap < safeFollowGap * 1.75f &&
                        leader.speed < self.desiredSpeed - 4f &&
                        self.targetLane == self.lane &&
                        self.signal == TurnSignal.NONE
                    ) {
                        val candidates = when (self.lane) {
                            2 -> listOf(1)
                            1 -> listOf(0, 2)
                            else -> listOf(1)
                        }
                        val chosen = candidates.firstOrNull { isLaneClear(self, it) }
                        if (chosen != null) {
                            self.targetLane = chosen
                            self.signal = if (chosen < self.lane) TurnSignal.LEFT else TurnSignal.RIGHT
                            self.signalPreTimer = 0.65f
                        }
                    }

                    if (self.targetLane != self.lane) {
                        if (self.signalPreTimer > 0f) {
                            self.signalPreTimer -= dt
                            if (!isLaneClear(self, self.targetLane)) {
                                self.targetLane = self.lane
                                self.signal = TurnSignal.NONE
                                self.signalPreTimer = 0f
                            }
                        } else {
                            val dir = if (self.targetLane > self.lateralY) 1f else -1f
                            self.lateralY += dir * dt * 1.15f
                            if ((dir > 0f && self.lateralY >= self.targetLane) ||
                                (dir < 0f && self.lateralY <= self.targetLane)
                            ) {
                                self.lateralY = self.targetLane.toFloat()
                                self.lane = self.targetLane
                                self.signal = TurnSignal.NONE
                            }
                        }
                    }

                    if (leader != null && minGap < safeFollowGap) {
                        val targetSpd = (leader.speed - 4f).coerceAtLeast(18f)
                        self.speed = (self.speed - 75f * dt).coerceAtLeast(targetSpd)
                        self.isBraking = self.speed > leader.speed + 1f || minGap < safeFollowGap * 0.72f
                    } else {
                        self.speed = (self.speed + 26f * dt).coerceAtMost(self.desiredSpeed)
                        self.isBraking = false
                    }
                }

                for (self in units) {
                    self.x += self.speed * dt

                    for (other in units) {
                        if (other.id == self.id) continue
                        if (abs(other.lateralY - self.lateralY) < 0.65f && other.x > self.x) {
                            val maxAllowedX = other.x - (other.length + self.length) / 2f - 16f
                            if (self.x > maxAllowedX) {
                                self.x = maxAllowedX
                                self.speed = self.speed.coerceAtMost(other.speed)
                                self.isBraking = true
                            }
                        }
                    }

                    if (self.x - self.length / 2f > w + 40f) {
                        val candidateLanes = listOf(self.lane, 0, 1, 2).distinct()
                        val openLane = candidateLanes.firstOrNull { ln ->
                            units.none { other ->
                                other.id != self.id &&
                                    (other.lane == ln || other.targetLane == ln) &&
                                    other.x < self.length * 1.8f + 40f
                            }
                        } ?: self.lane
                        val rearMost = units
                            .filter { it.id != self.id && (it.lane == openLane || it.targetLane == openLane) }
                            .minByOrNull { it.x }
                        val spawnX = if (rearMost != null) {
                            minOf(-self.length - 30f, rearMost.x - (rearMost.length + self.length) - 48f)
                        } else {
                            -self.length - 30f
                        }
                        self.x = spawnX
                        self.lane = openLane
                        self.targetLane = openLane
                        self.lateralY = openLane.toFloat()
                        self.signal = TurnSignal.NONE
                        self.signalPreTimer = 0f
                    }
                }

                frameTick++
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.highwayTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.inkPrimary
                    )
                    Text(
                        text = strings.highwaySubtitle,
                        fontSize = 12.sp,
                        color = palette.inkSecondary
                    )
                }
                IconButton(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = palette.navyPrimary
                    )
                }
            }

            val blinkerOn = (frameTick / 16) % 2 == 0

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                ) {
                    @Suppress("UNUSED_VARIABLE")
                    val tick = frameTick
                    roadWidthPx = size.width

                    val w = size.width
                    val h = size.height
                    val shoulderH = 12f
                    val roadTop = shoulderH
                    val roadH = h - shoulderH * 2f
                    val laneH = roadH / 3f

                    drawRect(palette.navyPrimary, size = Size(w, shoulderH))
                    drawRect(palette.navyPrimary, topLeft = Offset(0f, h - shoulderH), size = Size(w, shoulderH))
                    drawRect(palette.roadAsphalt, topLeft = Offset(0f, roadTop), size = Size(w, roadH))

                    drawLine(palette.solarOrange, Offset(0f, roadTop + 2f), Offset(w, roadTop + 2f), strokeWidth = 3f)
                    drawLine(palette.skyCyanBright, Offset(0f, h - shoulderH - 2f), Offset(w, h - shoulderH - 2f), strokeWidth = 3f)

                    for (div in 1..2) {
                        val y = roadTop + laneH * div
                        drawLine(
                            color = Color.White.copy(alpha = 0.50f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 18f), 0f)
                        )
                    }

                    for (unit in units.sortedBy { it.lateralY }) {
                        val cy = roadTop + laneH * (unit.lateralY + 0.5f)
                        drawOncfHighwayUnit(unit, cy, blinkerOn)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OncfSegmentedBar(
                    options = listOf(false to strings.trafficSmooth, true to strings.trafficDense),
                    selected = isDense,
                    onSelect = { isDense = it },
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "  ${units.size} ${strings.unitsCountLabel}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    color = palette.inkMuted
                )
            }
        }
    }
}

private fun DrawScope.drawOncfHighwayUnit(
    unit: HighwayUnit,
    centerY: Float,
    blinkerOn: Boolean
) {
    val left = unit.x - unit.length / 2f
    val top = centerY - unit.width / 2f
    val l = unit.length
    val w = unit.width

    drawRoundRect(
        color = Color.Black.copy(alpha = 0.30f),
        topLeft = Offset(left + 2f, top + 3f),
        size = Size(l, w),
        cornerRadius = CornerRadius(w * 0.28f, w * 0.28f)
    )

    when (unit.kind) {
        AutoVehicleKind.MOTO -> {
            drawRoundRect(Color(0xFF0F172A), Offset(left, centerY - 3.5f), Size(l, 7f), CornerRadius(3.5f, 3.5f))
            drawRoundRect(unit.bodyColor, Offset(left + l * 0.18f, top + 2f), Size(l * 0.58f, w - 4f), CornerRadius(6f, 6f))
            drawLine(Color(0xFFCBD5E1), Offset(left + l * 0.72f, top), Offset(left + l * 0.72f, top + w), strokeWidth = 2.8f, cap = StrokeCap.Round)
            drawCircle(Color.White, radius = w * 0.34f, center = Offset(left + l * 0.48f, centerY))
            drawCircle(Color(0xFFFEF08A), radius = 3f, center = Offset(left + l - 2f, centerY))
            drawCircle(if (unit.isBraking) Color(0xFFF43F5E) else Color(0xFF9F1239), radius = if (unit.isBraking) 3.5f else 2.2f, center = Offset(left + 2f, centerY))
        }
        AutoVehicleKind.COACH -> {
            drawRoundRect(unit.bodyColor, Offset(left, top), Size(l, w), CornerRadius(8f, 8f))
            drawRect(Color(0xFFFF6B00), Offset(left + 8f, centerY - 3f), Size(l - 28f, 6f))
            drawRoundRect(Color(0xFF0F172A), Offset(left + l * 0.80f, top + 3f), Size(l * 0.14f, w - 6f), CornerRadius(4f, 4f))
        }
        else -> {
            drawRoundRect(unit.bodyColor, Offset(left, top), Size(l, w), CornerRadius(w * 0.30f, w * 0.30f))
            drawRoundRect(Color(0xFF0F172A), Offset(left + l * 0.22f, top + 3.5f), Size(l * 0.56f, w - 7f), CornerRadius(4f, 4f))
            drawRoundRect(unit.bodyColor, Offset(left + l * 0.32f, top + 5.5f), Size(l * 0.30f, w - 11f), CornerRadius(3f, 3f))
            drawRect(unit.bodyColor, Offset(left + l * 0.62f, top - 2.5f), Size(4f, 3f))
            drawRect(unit.bodyColor, Offset(left + l * 0.62f, top + w - 0.5f), Size(4f, 3f))
        }
    }

    if (unit.kind != AutoVehicleKind.MOTO) {
        drawCircle(Color(0xFFFEF9C3), radius = 2.5f, center = Offset(left + l - 2f, top + 5f))
        drawCircle(Color(0xFFFEF9C3), radius = 2.5f, center = Offset(left + l - 2f, top + w - 5f))
        val brakeColor = if (unit.isBraking) Color(0xFFF43F5E) else Color(0xFF9F1239)
        val brakeR = if (unit.isBraking) 3.5f else 2.0f
        drawCircle(brakeColor, radius = brakeR, center = Offset(left + 2f, top + 5f))
        drawCircle(brakeColor, radius = brakeR, center = Offset(left + 2f, top + w - 5f))
    }

    if (blinkerOn && unit.signal != TurnSignal.NONE) {
        val by = if (unit.signal == TurnSignal.LEFT) top + 1.5f else top + w - 1.5f
        val amber = Color(0xFFF59E0B)
        drawCircle(amber.copy(alpha = 0.38f), radius = 7f, center = Offset(left + l - 3f, by))
        drawCircle(amber, radius = 3.2f, center = Offset(left + l - 3f, by))
        drawCircle(amber.copy(alpha = 0.38f), radius = 7f, center = Offset(left + 3f, by))
        drawCircle(amber, radius = 3.2f, center = Offset(left + 3f, by))
    }
}
