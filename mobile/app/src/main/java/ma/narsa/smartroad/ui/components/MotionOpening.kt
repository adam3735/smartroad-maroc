package ma.narsa.smartroad.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MotionOpening(onComplete: () -> Unit) {
    val petalBloom = remember { Animatable(0f) }
    val strapworkSweep = remember { Animatable(0f) }
    val ringRotation = remember { Animatable(-28f) }
    val outerStarsBloom = remember { Animatable(0f) }
    val sheenSweep = remember { Animatable(0f) }
    val crestSweep = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val exitScale = remember { Animatable(1f) }
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            petalBloom.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            strapworkSweep.animateTo(1f, tween(1150, easing = FastOutSlowInEasing))
        }
        launch {
            ringRotation.animateTo(0f, tween(1450, easing = FastOutSlowInEasing))
        }
        launch {
            delay(280)
            outerStarsBloom.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            delay(500)
            crestSweep.animateTo(1f, tween(750, easing = FastOutSlowInEasing))
        }
        launch {
            delay(650)
            titleAlpha.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
        }
        launch {
            delay(850)
            sheenSweep.animateTo(1f, tween(850, easing = FastOutSlowInEasing))
        }
        delay(2050)
        launch { exitScale.animateTo(1.16f, tween(340, easing = FastOutSlowInEasing)) }
        launch { exitAlpha.animateTo(0f, tween(340, easing = FastOutSlowInEasing)) }
        delay(350)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(exitAlpha.value)
            .scale(exitScale.value)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF091224), Color(0xFF121F3D), Color(0xFF0A1429))
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onComplete() }
    ) {
        // Full-Screen Moroccan Fassi Zellige Tessellation Lattice + Central 16-Fold Shamsa Mandala
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h * 0.42f
            val mandalaR = size.minDimension * 0.38f

            // 1. Background subtle Fassi 8-point Khatam tile grid
            val cellSize = size.minDimension * 0.22f
            val cols = (w / cellSize).toInt() + 2
            val rows = (h / cellSize).toInt() + 2
            val bgAlpha = 0.09f * strapworkSweep.value
            for (r in -1..rows) {
                for (c in -1..cols) {
                    val gx = c * cellSize + (if (r % 2 == 0) 0f else cellSize * 0.5f)
                    val gy = r * cellSize * 0.86f
                    drawKhatamStar(
                        center = Offset(gx, gy),
                        radius = cellSize * 0.34f,
                        fillColor = Color.Transparent,
                        strokeColor = Color(0xFF38BDF8).copy(alpha = bgAlpha),
                        strokeWidth = 1.4f
                    )
                }
            }

            // 2. Ambient ceramic radial halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0284C7).copy(alpha = 0.24f * petalBloom.value),
                        Color(0xFFFF6B00).copy(alpha = 0.12f * petalBloom.value),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = mandalaR * 1.5f
                ),
                radius = mandalaR * 1.5f,
                center = Offset(cx, cy)
            )

            // 3. Outer Ring of 8 Staggered Glazed Khatam Stars
            val orbitR = mandalaR * 0.84f * outerStarsBloom.value
            for (i in 0 until 8) {
                val angle = (i * 45.0 + ringRotation.value * 0.65) * PI / 180.0
                val starCenter = Offset(
                    cx + (orbitR * cos(angle)).toFloat(),
                    cy + (orbitR * sin(angle)).toFloat()
                )
                val isOrange = i % 2 == 0
                val tileColor = if (isOrange) Color(0xFFFF6B00) else Color(0xFF0EA5E9)
                drawKhatamStar(
                    center = starCenter,
                    radius = mandalaR * 0.17f * outerStarsBloom.value,
                    fillColor = tileColor.copy(alpha = 0.82f),
                    strokeColor = Color(0xFFF8FAFC).copy(alpha = 0.88f),
                    strokeWidth = 2.2f
                )
            }

            // 4. Inner 16-Petal Moroccan Shamsa Zellige Rosette
            rotate(degrees = ringRotation.value, pivot = Offset(cx, cy)) {
                val petalOuter = mandalaR * 0.62f * petalBloom.value
                val petalMid = mandalaR * 0.42f * petalBloom.value
                val petalInner = mandalaR * 0.22f * petalBloom.value

                for (i in 0 until 16) {
                    val aCenter = (i * 22.5) * PI / 180.0
                    val aLeft = ((i - 0.5) * 22.5) * PI / 180.0
                    val aRight = ((i + 0.5) * 22.5) * PI / 180.0

                    val pInner = Offset(cx + (petalInner * cos(aCenter)).toFloat(), cy + (petalInner * sin(aCenter)).toFloat())
                    val pLeft = Offset(cx + (petalMid * cos(aLeft)).toFloat(), cy + (petalMid * sin(aLeft)).toFloat())
                    val pTip = Offset(cx + (petalOuter * cos(aCenter)).toFloat(), cy + (petalOuter * sin(aCenter)).toFloat())
                    val pRight = Offset(cx + (petalMid * cos(aRight)).toFloat(), cy + (petalMid * sin(aRight)).toFloat())

                    val petalPath = Path().apply {
                        moveTo(pInner.x, pInner.y)
                        lineTo(pLeft.x, pLeft.y)
                        lineTo(pTip.x, pTip.y)
                        lineTo(pRight.x, pRight.y)
                        close()
                    }
                    val fillCol = when (i % 3) {
                        0 -> Color(0xFFFF6B00)
                        1 -> Color(0xFF0284C7)
                        else -> Color(0xFF1E3A8A)
                    }
                    drawPath(petalPath, color = fillCol.copy(alpha = 0.90f))
                    // Crisp Ivory Ceramic Grout ("Tafsil")
                    drawPath(petalPath, color = Color(0xFFF8FAFC).copy(alpha = 0.92f), style = Stroke(width = 2.4f, join = StrokeJoin.Miter))
                }
            }

            // 5. Counter-Rotating Interlaced {16/5} Tafsil Strapwork drawn live via PathMeasure
            rotate(degrees = -ringRotation.value * 0.8f, pivot = Offset(cx, cy)) {
                val strapR = mandalaR * 0.68f
                val strapPath = Path()
                val steps = 16
                val jump = 5
                var curIdx = 0
                for (k in 0..steps) {
                    val angle = (curIdx * (360.0 / steps) - 90.0) * PI / 180.0
                    val pt = Offset(cx + (strapR * cos(angle)).toFloat(), cy + (strapR * sin(angle)).toFloat())
                    if (k == 0) strapPath.moveTo(pt.x, pt.y) else strapPath.lineTo(pt.x, pt.y)
                    curIdx = (curIdx + jump) % steps
                }
                val segPath = Path()
                val pm = PathMeasure()
                pm.setPath(strapPath, false)
                pm.getSegment(0f, pm.length * strapworkSweep.value, segPath, true)
                drawPath(
                    segPath,
                    color = Color(0xFFFBBF24).copy(alpha = 0.85f),
                    style = Stroke(width = 2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // 6. Central Navy Medallion framing the SmartRoad Crest
            drawCircle(
                color = Color(0xFF0D172A),
                radius = mandalaR * 0.30f * petalBloom.value,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color(0xFFF8FAFC).copy(alpha = 0.9f),
                radius = mandalaR * 0.30f * petalBloom.value,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )

            // 7. Glazed Ceramic Specular Sheen Sweep
            if (sheenSweep.value in 0.01f..0.99f) {
                val sx = (cx - mandalaR * 1.4f) + (mandalaR * 2.8f * sheenSweep.value)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.28f), Color.Transparent),
                        start = Offset(sx - 60f, cy - mandalaR),
                        end = Offset(sx + 60f, cy + mandalaR)
                    ),
                    start = Offset(sx - 60f, cy - mandalaR),
                    end = Offset(sx + 60f, cy + mandalaR),
                    strokeWidth = 70f
                )
            }
        }

        // Center SmartRoad Crest + Bilingual Title Lockup
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(modifier = Modifier.size(72.dp)) {
                val w = size.width
                val h = size.height
                val leftWing = Path().apply {
                    moveTo(w * 0.20f, h * 0.82f)
                    cubicTo(w * 0.20f, h * 0.56f, w * 0.64f, h * 0.54f, w * 0.64f, h * 0.38f)
                    cubicTo(w * 0.64f, h * 0.26f, w * 0.54f, h * 0.20f, w * 0.50f, h * 0.16f)
                }
                val segL = Path()
                val pmL = PathMeasure()
                pmL.setPath(leftWing, false)
                pmL.getSegment(0f, pmL.length * crestSweep.value, segL, true)
                drawPath(segL, Color(0xFF38BDF8), style = Stroke(9f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                val rightWing = Path().apply {
                    moveTo(w * 0.80f, h * 0.82f)
                    cubicTo(w * 0.80f, h * 0.56f, w * 0.36f, h * 0.54f, w * 0.36f, h * 0.38f)
                    cubicTo(w * 0.36f, h * 0.26f, w * 0.46f, h * 0.20f, w * 0.50f, h * 0.16f)
                }
                val segR = Path()
                val pmR = PathMeasure()
                pmR.setPath(rightWing, false)
                pmR.getSegment(0f, pmR.length * crestSweep.value, segR, true)
                drawPath(segR, Color(0xFFFF6B00), style = Stroke(9f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                if (crestSweep.value > 0.65f) {
                    val a = ((crestSweep.value - 0.65f) / 0.35f).coerceIn(0f, 1f)
                    val apex = Path().apply {
                        moveTo(w * 0.50f, h * 0.08f)
                        lineTo(w * 0.58f, h * 0.22f)
                        lineTo(w * 0.50f, h * 0.30f)
                        lineTo(w * 0.42f, h * 0.22f)
                        close()
                    }
                    drawPath(apex, Color.White.copy(alpha = a))
                }
            }

            Spacer(Modifier.height(145.dp))

            Column(
                modifier = Modifier.alpha(titleAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SmartRoad",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                    Text(
                        text = "Maroc",
                        color = Color(0xFFFF6B00),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    )
                }
                Text(
                    text = "طريق السلامة • المرصد الوطني نارسا",
                    color = Color(0xFF7DD3FC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "OBSERVATOIRE NARSA • MOBILITÉ & SÉCURITÉ",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.0.sp
                )
            }
        }
    }
}

private fun DrawScope.drawKhatamStar(
    center: Offset,
    radius: Float,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Float
) {
    if (radius <= 0.5f) return
    val half = radius * 1.38f
    for (rot in listOf(0f, 45f)) {
        rotate(degrees = rot, pivot = center) {
            if (fillColor != Color.Transparent) {
                drawRect(
                    color = fillColor,
                    topLeft = Offset(center.x - half / 2f, center.y - half / 2f),
                    size = androidx.compose.ui.geometry.Size(half, half)
                )
            }
            drawRect(
                color = strokeColor,
                topLeft = Offset(center.x - half / 2f, center.y - half / 2f),
                size = androidx.compose.ui.geometry.Size(half, half),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
