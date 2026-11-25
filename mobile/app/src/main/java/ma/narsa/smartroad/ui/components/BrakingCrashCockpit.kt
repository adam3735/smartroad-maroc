package ma.narsa.smartroad.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private enum class CockpitPhase { READY, COASTING, BRAKING, STOPPED_SAFE, CRASHED }
private enum class CockpitParticleKind { TIRE_SMOKE, RAIN_SPRAY, SPARK, GLASS, HOOD_SMOKE }

private class CockpitParticle(
    var yMarker: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    var life: Float,
    val maxLife: Float,
    val kind: CockpitParticleKind,
    val color: Color
)

@Composable
fun BrakingCrashCockpit(modifier: Modifier = Modifier) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val haptics = LocalHapticFeedback.current
    val textMeasurer = rememberTextMeasurer()

    var speedKmh by remember { mutableIntStateOf(90) }
    var vehicleType by remember { mutableStateOf("Berline") }
    var surface by remember { mutableStateOf("Sec") }
    var manualMode by remember { mutableStateOf(false) }

    val massKg = when (vehicleType) {
        "Autocar" -> 12500f
        "Moto" -> 220f
        else -> 1350f
    }
    val mu = when (surface) {
        "Pluie" -> 0.42f
        "Nuit" -> 0.70f
        else -> 0.78f
    } * if (vehicleType == "Moto") 0.92f else if (vehicleType == "Autocar") 0.85f else 1.0f
    val reactionSec = if (surface == "Nuit") 1.3f else 1.0f
    val obstacleDistanceM = 85f

    var phase by remember { mutableStateOf(CockpitPhase.READY) }
    var posMeters by remember { mutableFloatStateOf(0f) }
    var currentSpeedMs by remember { mutableFloatStateOf(speedKmh / 3.6f) }
    var elapsedSec by remember { mutableFloatStateOf(0f) }
    var reactionDistM by remember { mutableFloatStateOf(0f) }
    var brakingDistM by remember { mutableFloatStateOf(0f) }
    var impactSpeedKmh by remember { mutableIntStateOf(0) }
    var impactEnergyKj by remember { mutableIntStateOf(0) }
    var obstaclePushM by remember { mutableFloatStateOf(0f) }
    var obstacleVelMs by remember { mutableFloatStateOf(0f) }
    var crumplePx by remember { mutableFloatStateOf(0f) }
    var pitchDeg by remember { mutableFloatStateOf(0f) }
    var wheelAngle by remember { mutableFloatStateOf(0f) }
    var isBrakeHeld by remember { mutableStateOf(false) }
    var isBrakingNow by remember { mutableStateOf(false) }
    var frameTick by remember { mutableIntStateOf(0) }
    var runId by remember { mutableIntStateOf(0) }
    val particles = remember { mutableStateListOf<CockpitParticle>() }

    fun resetSim() {
        phase = CockpitPhase.READY
        posMeters = 0f
        currentSpeedMs = speedKmh / 3.6f
        elapsedSec = 0f
        reactionDistM = 0f
        brakingDistM = 0f
        impactSpeedKmh = 0
        impactEnergyKj = 0
        obstaclePushM = 0f
        obstacleVelMs = 0f
        crumplePx = 0f
        pitchDeg = 0f
        isBrakeHeld = false
        isBrakingNow = false
        particles.clear()
        frameTick++
    }

    fun startSim() {
        resetSim()
        phase = CockpitPhase.COASTING
        runId++
    }

    LaunchedEffect(speedKmh, vehicleType, surface, manualMode) {
        if (phase == CockpitPhase.READY) {
            currentSpeedMs = speedKmh / 3.6f
        }
    }

    LaunchedEffect(runId) {
        if (runId == 0) return@LaunchedEffect
        var lastNanos = 0L
        var smokeCooldown = 0f
        var hoodCooldown = 0f

        while (true) {
            withFrameNanos { now ->
                if (lastNanos == 0L) {
                    lastNanos = now
                    return@withFrameNanos
                }
                val dt = ((now - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.04f)
                lastNanos = now

                when (phase) {
                    CockpitPhase.COASTING -> {
                        elapsedSec += dt
                        posMeters += currentSpeedMs * dt
                        reactionDistM = posMeters
                        wheelAngle += currentSpeedMs * dt * 36f
                        pitchDeg += (0f - pitchDeg) * 10f * dt
                        isBrakingNow = false

                        val triggerBrake = if (manualMode) isBrakeHeld else (elapsedSec >= reactionSec)
                        if (triggerBrake) {
                            phase = CockpitPhase.BRAKING
                        } else if (posMeters >= obstacleDistanceM) {
                            posMeters = obstacleDistanceM
                            phase = CockpitPhase.CRASHED
                            impactSpeedKmh = (currentSpeedMs * 3.6f).roundToInt()
                            impactEnergyKj = (0.5f * massKg * currentSpeedMs * currentSpeedMs / 1000f).roundToInt()
                            obstacleVelMs = currentSpeedMs * (massKg / (massKg + 1350f)) * 0.72f
                            crumplePx = (currentSpeedMs * 0.42f).coerceIn(6f, 16f)
                            pitchDeg = -6.5f
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            spawnDebris(particles, impactEnergyKj)
                        }
                    }

                    CockpitPhase.BRAKING -> {
                        val activeBrake = if (manualMode) isBrakeHeld else true
                        isBrakingNow = activeBrake
                        if (activeBrake) {
                            val decel = mu * 9.81f
                            currentSpeedMs = (currentSpeedMs - decel * dt).coerceAtLeast(0f)
                            pitchDeg += (3.4f - pitchDeg) * 12f * dt
                        } else {
                            pitchDeg += (0f - pitchDeg) * 10f * dt
                        }

                        posMeters += currentSpeedMs * dt
                        brakingDistM = (posMeters - reactionDistM).coerceAtLeast(0f)
                        wheelAngle += currentSpeedMs * dt * 28f

                        smokeCooldown -= dt
                        if (activeBrake && currentSpeedMs > 1.5f && smokeCooldown <= 0f) {
                            smokeCooldown = 0.03f
                            val isRain = surface == "Pluie"
                            repeat(2) {
                                particles.add(
                                    CockpitParticle(
                                        yMarker = if (it == 0) 0f else 1f,
                                        vx = -(20f + Random.nextFloat() * 26f),
                                        vy = -(10f + Random.nextFloat() * 22f),
                                        radius = 4f + Random.nextFloat() * 4f,
                                        alpha = if (isRain) 0.58f else 0.45f,
                                        life = 0f,
                                        maxLife = 0.55f + Random.nextFloat() * 0.25f,
                                        kind = if (isRain) CockpitParticleKind.RAIN_SPRAY else CockpitParticleKind.TIRE_SMOKE,
                                        color = if (isRain) Color(0xFF7DD3FC) else Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }

                        if (posMeters >= obstacleDistanceM) {
                            posMeters = obstacleDistanceM
                            isBrakingNow = false
                            if (currentSpeedMs > 0.6f) {
                                phase = CockpitPhase.CRASHED
                                impactSpeedKmh = (currentSpeedMs * 3.6f).roundToInt()
                                impactEnergyKj = (0.5f * massKg * currentSpeedMs * currentSpeedMs / 1000f).roundToInt()
                                obstacleVelMs = currentSpeedMs * (massKg / (massKg + 1350f)) * 0.72f
                                crumplePx = (currentSpeedMs * 0.42f).coerceIn(6f, 16f)
                                pitchDeg = -6.5f
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                spawnDebris(particles, impactEnergyKj)
                            } else {
                                currentSpeedMs = 0f
                                phase = CockpitPhase.STOPPED_SAFE
                            }
                        } else if (currentSpeedMs <= 0.05f && activeBrake) {
                            currentSpeedMs = 0f
                            isBrakingNow = false
                            phase = CockpitPhase.STOPPED_SAFE
                        }
                    }

                    CockpitPhase.STOPPED_SAFE -> {
                        pitchDeg += (0f - pitchDeg) * 10f * dt
                        isBrakingNow = false
                    }

                    CockpitPhase.CRASHED -> {
                        if (obstacleVelMs > 0.05f) {
                            obstaclePushM += obstacleVelMs * dt
                            obstacleVelMs = (obstacleVelMs - 7.5f * dt).coerceAtLeast(0f)
                        }
                        pitchDeg += (0f - pitchDeg) * 7f * dt

                        hoodCooldown -= dt
                        if (hoodCooldown <= 0f) {
                            hoodCooldown = 0.14f
                            particles.add(
                                CockpitParticle(
                                    yMarker = 0.5f,
                                    vx = -8f + Random.nextFloat() * 16f,
                                    vy = -(22f + Random.nextFloat() * 18f),
                                    radius = 5f + Random.nextFloat() * 5f,
                                    alpha = 0.46f,
                                    life = 0f,
                                    maxLife = 1.1f,
                                    kind = CockpitParticleKind.HOOD_SMOKE,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    CockpitPhase.READY -> {}
                }

                val iter = particles.iterator()
                while (iter.hasNext()) {
                    val p = iter.next()
                    p.life += dt
                    if (p.life >= p.maxLife) {
                        iter.remove()
                    } else if (p.kind == CockpitParticleKind.SPARK || p.kind == CockpitParticleKind.GLASS) {
                        p.vy += 180f * dt
                    } else {
                        p.radius += 11f * dt
                    }
                }

                frameTick++
            }
            if (phase == CockpitPhase.STOPPED_SAFE && particles.isEmpty() && abs(pitchDeg) < 0.05f) {
                break
            }
        }
    }

    val liveKmh = if (phase == CockpitPhase.CRASHED) 0 else (currentSpeedMs * 3.6f).roundToInt()
    val gForce = if (isBrakingNow) mu else 0f
    val v0 = speedKmh / 3.6f
    val theoTotal = (v0 * reactionSec + (v0 * v0) / (2f * mu * 9.81f)).roundToInt()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpeedometerArcGauge(
                title = strings.speedInstant,
                valueText = "$liveKmh km/h",
                subtitle = "${strings.speedTarget} : $speedKmh km/h",
                fraction = (liveKmh / 140f).coerceIn(0f, 1f),
                arcColor = if (phase == CockpitPhase.CRASHED) palette.coralAlert else palette.solarOrange,
                modifier = Modifier.weight(1f)
            )
            SpeedometerArcGauge(
                title = strings.decelTitle,
                valueText = String.format(Locale.US, "%.2f g", gForce),
                subtitle = String.format(Locale.US, "${strings.gripLabel} = %.2f", mu),
                fraction = (gForce / 1.0f).coerceIn(0f, 1f),
                arcColor = palette.skyCyan,
                modifier = Modifier.weight(1f)
            )
        }

        OncfSegmentedBar(
            options = listOf(false to strings.modeAuto, true to strings.modeManual),
            selected = manualMode,
            onSelect = { manualMode = it; resetSim() }
        )
        OncfSegmentedBar(
            options = listOf("Berline" to strings.vehSedan, "Autocar" to strings.vehCoach, "Moto" to strings.vehMoto),
            selected = vehicleType,
            onSelect = { vehicleType = it; resetSim() }
        )
        OncfSegmentedBar(
            options = listOf("Sec" to strings.surfDry, "Pluie" to strings.surfRain, "Nuit" to strings.surfNight),
            selected = surface,
            onSelect = { surface = it; resetSim() }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.initialSpeed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.inkSecondary)
            Text("$speedKmh km/h", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = palette.solarOrange)
        }
        Slider(
            value = speedKmh.toFloat(),
            onValueChange = {
                speedKmh = it.roundToInt()
                if (phase == CockpitPhase.READY) currentSpeedMs = speedKmh / 3.6f
            },
            valueRange = 30f..130f,
            colors = SliderDefaults.colors(thumbColor = palette.solarOrange, activeTrackColor = palette.solarOrange)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { speedKmh = 50; resetSim() },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(38.dp)
            ) { Text(strings.presetUrban, fontSize = 12.sp, color = palette.inkPrimary) }
            OutlinedButton(
                onClick = { speedKmh = 110; resetSim() },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(38.dp)
            ) { Text(strings.presetHighway, fontSize = 12.sp, color = palette.inkPrimary) }
        }

        val hazardOn = (frameTick / 18) % 2 == 0
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (surface == "Nuit") Color(0xFF070D1B) else palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            ) {
                @Suppress("UNUSED_VARIABLE")
                val tick = frameTick
                val w = size.width
                val h = size.height
                val roadTop = h * 0.48f
                val roadH = h * 0.38f
                val roadBottom = roadTop + roadH

                val startX = 36f
                val obstacleX = w * 0.72f
                val pxPerMeter = (obstacleX - startX) / obstacleDistanceM

                if (surface == "Pluie") {
                    for (r in 0..22) {
                        val rx = ((r * 43f) + frameTick * 3f) % w
                        val ry = ((r * 29f) + frameTick * 6f) % roadTop
                        drawLine(Color(0xFF38BDF8).copy(alpha = 0.45f), Offset(rx, ry), Offset(rx - 6f, ry + 14f), strokeWidth = 1.6f)
                    }
                }

                drawRect(palette.roadAsphalt, Offset(0f, roadTop), Size(w, roadH))
                drawLine(
                    Color.White.copy(alpha = 0.55f),
                    Offset(16f, roadTop + roadH / 2f),
                    Offset(w - 16f, roadTop + roadH / 2f),
                    strokeWidth = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f)
                )

                listOf(0, 25, 50, 85).forEach { m ->
                    val mx = startX + m * pxPerMeter
                    drawLine(palette.inkMuted.copy(alpha = 0.55f), Offset(mx, roadBottom), Offset(mx, roadBottom + 6f), strokeWidth = 1.5f)
                    val lbl = textMeasurer.measure("${m}m", TextStyle(color = palette.inkMuted, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace))
                    drawText(lbl, topLeft = Offset(mx - lbl.size.width / 2f, roadBottom + 7f))
                }

                if (reactionDistM > 0.5f) {
                    val rW = (reactionDistM * pxPerMeter).coerceAtMost(obstacleX - startX)
                    drawRect(palette.amberSignal.copy(alpha = 0.25f), Offset(startX, roadTop), Size(rW, roadH))
                }
                if (brakingDistM > 0.5f) {
                    val bStart = startX + reactionDistM * pxPerMeter
                    val bW = (brakingDistM * pxPerMeter).coerceAtMost(obstacleX - bStart)
                    val bCol = if (phase == CockpitPhase.CRASHED) palette.coralAlert else palette.skyCyan
                    drawRect(bCol.copy(alpha = 0.28f), Offset(bStart, roadTop), Size(bW, roadH))
                }

                val obsW = 92f
                val obsH = 40f
                val obsDrawX = obstacleX + obstaclePushM * pxPerMeter
                val obsDrawY = roadTop + (roadH - obsH) / 2f + 2f
                drawCockpitSideVehicle(
                    x = obsDrawX,
                    y = obsDrawY,
                    width = obsW,
                    height = obsH,
                    bodyColor = Color(0xFF64748B),
                    kind = "Berline",
                    wheelAngle = obstaclePushM * 25f,
                    isBraking = false,
                    isNight = surface == "Nuit",
                    pitchDeg = if (phase == CockpitPhase.CRASHED && obstacleVelMs > 0.2f) 2.5f else 0f,
                    crumpleLeftPx = if (phase == CockpitPhase.CRASHED) crumplePx * 0.5f else 0f,
                    showAirbag = false,
                    hazardBlink = phase == CockpitPhase.CRASHED && hazardOn
                )

                val vehW = when (vehicleType) {
                    "Moto" -> 76f
                    "Autocar" -> 124f
                    else -> 98f
                }
                val vehH = when (vehicleType) {
                    "Moto" -> 38f
                    "Autocar" -> 46f
                    else -> 40f
                }
                val rawFrontX = startX + posMeters * pxPerMeter
                val clampedFrontX = if (phase == CockpitPhase.CRASHED) minOf(rawFrontX, obsDrawX - 2f) else rawFrontX
                val vehLeftX = clampedFrontX - vehW
                val vehDrawY = roadTop + (roadH - vehH) / 2f + 2f

                drawCockpitSideVehicle(
                    x = vehLeftX,
                    y = vehDrawY,
                    width = vehW,
                    height = vehH,
                    bodyColor = if (phase == CockpitPhase.CRASHED) palette.coralAlert else palette.solarOrange,
                    kind = vehicleType,
                    wheelAngle = wheelAngle,
                    isBraking = isBrakingNow,
                    isNight = surface == "Nuit",
                    pitchDeg = pitchDeg,
                    crumpleRightPx = if (phase == CockpitPhase.CRASHED) crumplePx else 0f,
                    showAirbag = phase == CockpitPhase.CRASHED,
                    hazardBlink = phase == CockpitPhase.CRASHED && hazardOn
                )

                for (p in particles) {
                    val frac = (p.life / p.maxLife).coerceIn(0f, 1f)
                    val alpha = p.alpha * (1f - frac)
                    when (p.kind) {
                        CockpitParticleKind.TIRE_SMOKE, CockpitParticleKind.RAIN_SPRAY -> {
                            val wx = if (p.yMarker < 0.5f) vehLeftX + vehW * 0.78f else vehLeftX + vehW * 0.22f
                            drawCircle(p.color.copy(alpha = alpha), p.radius, Offset(wx + p.vx * p.life, vehDrawY + vehH - 6f + p.vy * p.life))
                        }
                        CockpitParticleKind.HOOD_SMOKE -> {
                            drawCircle(p.color.copy(alpha = alpha), p.radius, Offset(clampedFrontX - 10f + p.vx * p.life, vehDrawY + 8f + p.vy * p.life))
                        }
                        CockpitParticleKind.SPARK, CockpitParticleKind.GLASS -> {
                            drawCircle(p.color.copy(alpha = alpha), p.radius, Offset(obsDrawX + p.vx * p.life, vehDrawY + vehH * 0.5f + p.vy * p.life + 90f * p.life * p.life))
                        }
                    }
                }

                if (phase == CockpitPhase.CRASHED && obstacleVelMs > 0.5f) {
                    drawCircle(palette.coralAlert.copy(alpha = 0.35f), 24f, Offset(obsDrawX, vehDrawY + vehH * 0.5f))
                }

                val hudStr = when (phase) {
                    CockpitPhase.READY -> "$speedKmh km/h • ${strings.hudReady}"
                    CockpitPhase.COASTING -> if (manualMode) strings.hudObstacleWarning else "${strings.hudReactionTime} • $liveKmh km/h"
                    CockpitPhase.BRAKING -> if (isBrakingNow) "${strings.hudBrakingActive} • $liveKmh km/h" else "${strings.hudCoasting} • $liveKmh km/h"
                    CockpitPhase.STOPPED_SAFE -> "${strings.hudStoppedSafe} (${(obstacleDistanceM - posMeters).roundToInt()} m)"
                    CockpitPhase.CRASHED -> "${strings.hudImpact} $impactSpeedKmh km/h • $impactEnergyKj kJ"
                }
                val hudColor = when (phase) {
                    CockpitPhase.CRASHED -> palette.coralAlert
                    CockpitPhase.STOPPED_SAFE -> palette.skyCyan
                    CockpitPhase.COASTING -> palette.amberSignal
                    else -> palette.inkPrimary
                }
                val hudText = textMeasurer.measure(hudStr, TextStyle(color = hudColor, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                drawText(hudText, topLeft = Offset(14f, 12f))
            }
        }

        if (manualMode) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { startSim() },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.navyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text(if (phase == CockpitPhase.READY) strings.btnStart else strings.btnRestart, color = Color.White, fontWeight = FontWeight.Bold) }

                val canHold = phase == CockpitPhase.COASTING || phase == CockpitPhase.BRAKING
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                !canHold -> palette.surfaceMuted
                                isBrakeHeld -> palette.solarOrange
                                else -> palette.solarOrangeSoft
                            }
                        )
                        .border(1.dp, if (canHold) palette.solarOrange else palette.border, RoundedCornerShape(12.dp))
                        .pointerInput(canHold) {
                            if (canHold) {
                                detectTapGestures(
                                    onPress = {
                                        isBrakeHeld = true
                                        tryAwaitRelease()
                                        isBrakeHeld = false
                                    }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBrakeHeld) strings.holdBrakeActive else strings.holdBrakeIdle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = when {
                            !canHold -> palette.inkMuted
                            isBrakeHeld -> Color.White
                            else -> palette.solarOrangeDeep
                        }
                    )
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { startSim() },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.solarOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) { Text(if (phase == CockpitPhase.READY) strings.btnLaunchTest else strings.btnRelaunchTest, fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = { resetSim() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(46.dp)
                ) { Text(strings.btnReset, color = palette.inkPrimary) }
            }
        }

        PerforatedTicketCard(
            headerTag = strings.telemetryTag,
            accentColor = if (phase == CockpitPhase.CRASHED) palette.coralAlert else palette.skyCyan,
            stubLeft = "${strings.theoDistPrefix} : $theoTotal m",
            stubRight = if (phase == CockpitPhase.CRASHED) "$impactEnergyKj ${strings.impactEnergyUnit}" else "${(obstacleDistanceM - posMeters).roundToInt().coerceAtLeast(0)} ${strings.marginUnit}"
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(strings.reactionLabel, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                    Text("${reactionDistM.roundToInt()} m", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = palette.amberSignal)
                }
                Column {
                    Text(strings.brakingLabel, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                    Text("${brakingDistM.roundToInt()} m", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = palette.skyCyan)
                }
                Column {
                    Text(strings.totalDistLabel, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted)
                    Text("${posMeters.roundToInt()} m", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = palette.inkPrimary)
                }
            }
        }
    }
}

private fun spawnDebris(particles: MutableList<CockpitParticle>, energyKj: Int) {
    val count = (energyKj / 25).coerceIn(10, 24)
    repeat(count) { i ->
        val isSpark = i % 2 == 0
        particles.add(
            CockpitParticle(
                yMarker = 0f,
                vx = -40f + Random.nextFloat() * 140f,
                vy = -(50f + Random.nextFloat() * 110f),
                radius = if (isSpark) 2.5f + Random.nextFloat() * 2f else 3f + Random.nextFloat() * 2.5f,
                alpha = 0.95f,
                life = 0f,
                maxLife = 0.45f + Random.nextFloat() * 0.45f,
                kind = if (isSpark) CockpitParticleKind.SPARK else CockpitParticleKind.GLASS,
                color = if (isSpark) Color(0xFFFFB703) else Color(0xFFBAE6FD)
            )
        )
    }
}

private fun DrawScope.drawCockpitSideVehicle(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    bodyColor: Color,
    kind: String,
    wheelAngle: Float,
    isBraking: Boolean,
    isNight: Boolean,
    pitchDeg: Float,
    crumpleRightPx: Float = 0f,
    crumpleLeftPx: Float = 0f,
    showAirbag: Boolean = false,
    hazardBlink: Boolean = false
) {
    val effectiveX = x + crumpleLeftPx
    val effectiveW = (width - crumpleRightPx - crumpleLeftPx).coerceAtLeast(width * 0.65f)
    val pivot = Offset(effectiveX + effectiveW / 2f, y + height * 0.78f)

    rotate(degrees = pitchDeg, pivot = pivot) {
        if (isNight && !showAirbag) {
            val cone = Path().apply {
                moveTo(effectiveX + effectiveW - 2f, y + height * 0.55f)
                lineTo(effectiveX + effectiveW + 130f, y - 10f)
                lineTo(effectiveX + effectiveW + 130f, y + height + 10f)
                close()
            }
            drawPath(
                cone,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFEF08A).copy(alpha = 0.36f), Color.Transparent),
                    startX = effectiveX + effectiveW,
                    endX = effectiveX + effectiveW + 130f
                )
            )
        }

        val wheelR = height * (if (kind == "Moto") 0.25f else 0.21f)
        val wheelY = y + height - wheelR

        when (kind) {
            "Moto" -> {
                val body = Path().apply {
                    moveTo(effectiveX + effectiveW * 0.20f, wheelY)
                    lineTo(effectiveX + effectiveW * 0.38f, y + height * 0.42f)
                    lineTo(effectiveX + effectiveW * 0.74f, y + height * 0.32f)
                    lineTo(effectiveX + effectiveW * 0.80f, wheelY)
                    close()
                }
                drawPath(body, bodyColor)
                drawCircle(Color.White, radius = height * 0.14f, center = Offset(effectiveX + effectiveW * 0.58f, y + height * 0.16f))
            }
            "Autocar" -> {
                drawRoundRect(bodyColor, Offset(effectiveX, y + height * 0.12f), Size(effectiveW, wheelY - (y + height * 0.12f)), CornerRadius(7f, 7f))
                drawRoundRect(Color(0xFFDBEAFE).copy(alpha = 0.88f), Offset(effectiveX + 10f, y + height * 0.22f), Size(effectiveW - 18f, height * 0.26f), CornerRadius(3f, 3f))
                if (showAirbag) {
                    drawCircle(Color.White, radius = height * 0.13f, center = Offset(effectiveX + effectiveW * 0.85f, y + height * 0.36f))
                }
            }
            else -> {
                val bodyTop = y + height * 0.36f
                drawRoundRect(bodyColor, Offset(effectiveX, bodyTop), Size(effectiveW, wheelY - bodyTop), CornerRadius(6f, 6f))
                val cabin = Path().apply {
                    moveTo(effectiveX + effectiveW * 0.18f, bodyTop + 1f)
                    lineTo(effectiveX + effectiveW * 0.32f, y + height * 0.06f)
                    lineTo(effectiveX + effectiveW * 0.68f, y + height * 0.06f)
                    lineTo(effectiveX + effectiveW * 0.80f, bodyTop + 1f)
                    close()
                }
                drawPath(cabin, bodyColor)
                val window = Path().apply {
                    moveTo(effectiveX + effectiveW * 0.24f, bodyTop)
                    lineTo(effectiveX + effectiveW * 0.34f, y + height * 0.12f)
                    lineTo(effectiveX + effectiveW * 0.65f, y + height * 0.12f)
                    lineTo(effectiveX + effectiveW * 0.76f, bodyTop)
                    close()
                }
                drawPath(window, Color(0xFFDBEAFE).copy(alpha = 0.88f))
                if (showAirbag) {
                    drawCircle(Color.White, radius = height * 0.13f, center = Offset(effectiveX + effectiveW * 0.63f, y + height * 0.27f))
                }
            }
        }

        if (isBraking) {
            drawCircle(Color(0xFFF43F5E).copy(alpha = 0.45f), 12f, Offset(effectiveX + 2f, y + height * 0.50f))
            drawCircle(Color(0xFFF43F5E), 4f, Offset(effectiveX + 3f, y + height * 0.50f))
        }

        if (hazardBlink) {
            drawCircle(Color(0xFFF59E0B), 4.5f, Offset(effectiveX + 4f, y + height * 0.50f))
            drawCircle(Color(0xFFF59E0B), 4.5f, Offset(effectiveX + effectiveW - 4f, y + height * 0.50f))
        }

        val wPositions = listOf(effectiveX + effectiveW * 0.22f, effectiveX + effectiveW * 0.78f)
        wPositions.forEach { wx ->
            val center = Offset(wx, wheelY)
            drawCircle(Color(0xFF0F172A), radius = wheelR, center = center)
            drawCircle(
                if (isBraking) Color(0xFFFF6B00).copy(alpha = 0.9f) else Color(0xFFCBD5E1),
                radius = wheelR * 0.52f,
                center = center
            )
            val rad = (wheelAngle * PI / 180.0).toFloat()
            drawLine(
                Color(0xFF334155),
                Offset(center.x - cos(rad) * wheelR * 0.45f, center.y - sin(rad) * wheelR * 0.45f),
                Offset(center.x + cos(rad) * wheelR * 0.45f, center.y + sin(rad) * wheelR * 0.45f),
                strokeWidth = 2f
            )
        }
    }
}
