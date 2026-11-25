package ma.narsa.smartroad.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.ui.i18n.AppLanguage
import ma.narsa.smartroad.ui.i18n.LocalAppLanguage
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.theme.LocalOncfPalette
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun formatNumberFr(value: Int): String =
    NumberFormat.getNumberInstance(Locale.FRANCE).format(value)

@Composable
fun OncfBrandMark(size: Dp = 32.dp, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
    ) {
        val w = this.size.width
        val h = this.size.height
        drawRect(color = Color(0xFF121F3D))
        val leftWing = Path().apply {
            moveTo(w * 0.22f, h * 0.78f)
            cubicTo(w * 0.22f, h * 0.56f, w * 0.64f, h * 0.54f, w * 0.64f, h * 0.40f)
            cubicTo(w * 0.64f, h * 0.29f, w * 0.55f, h * 0.24f, w * 0.50f, h * 0.20f)
        }
        drawPath(leftWing, Color(0xFF38BDF8), style = Stroke(w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val rightWing = Path().apply {
            moveTo(w * 0.78f, h * 0.78f)
            cubicTo(w * 0.78f, h * 0.56f, w * 0.36f, h * 0.54f, w * 0.36f, h * 0.40f)
            cubicTo(w * 0.36f, h * 0.29f, w * 0.45f, h * 0.24f, w * 0.50f, h * 0.20f)
        }
        drawPath(rightWing, Color(0xFFFF6B00), style = Stroke(w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val apex = Path().apply {
            moveTo(w * 0.50f, h * 0.13f)
            lineTo(w * 0.57f, h * 0.24f)
            lineTo(w * 0.50f, h * 0.31f)
            lineTo(w * 0.43f, h * 0.24f)
            close()
        }
        drawPath(apex, Color.White)
    }
}

@Composable
fun OncfCurvedHeader(
    title: String,
    subtitle: String,
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit,
    extraBottomPadding: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val lang = LocalAppLanguage.current

    Box(modifier = modifier.fillMaxWidth()) {
        // Curved Royal Navy Canvas with 16-pointed Zellige Rosette Filigree
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val curveHeight = 26f

            val headerPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h - curveHeight)
                quadraticBezierTo(w / 2f, h + curveHeight * 0.9f, 0f, h - curveHeight)
                close()
            }
            drawPath(
                path = headerPath,
                brush = Brush.verticalGradient(
                    colors = listOf(palette.navyDeep, palette.navyPrimary, palette.navyMid)
                )
            )

            // 16-fold Zellige Rosette Watermark
            val cx = w * 0.88f
            val cy = h * 0.36f
            val r = h * 0.48f
            for (rot in listOf(0f, 22.5f, 45f, 67.5f)) {
                rotate(degrees = rot, pivot = Offset(cx, cy)) {
                    drawRect(
                        color = Color.White.copy(alpha = 0.045f),
                        topLeft = Offset(cx - r / 2f, cy - r / 2f),
                        size = Size(r, r),
                        style = Stroke(width = 1.6f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 14.dp, top = 14.dp, bottom = extraBottomPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OncfBrandMark(size = 32.dp)
                    Column {
                        Text(
                            text = strings.appBrand,
                            color = Color(0xFF38BDF8),
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        )
                        Text(
                            text = strings.observatoireSub,
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Interactive FR / عربي Toggle Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable { onToggleLanguage() }
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (lang == AppLanguage.FR) palette.solarOrange else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "FR",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (lang == AppLanguage.AR) palette.solarOrange else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "عربي",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onToggleNight) {
                        Icon(
                            imageVector = if (isNight) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Mode Jour/Nuit",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FloatingRouteFilterCard(
    selectedZone: String,
    selectedYear: Int,
    onZoneChange: (String) -> Unit,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    val zones = listOf("Total", "Agglomeration", "Hors_agglomeration")
    val nextZone = {
        val idx = zones.indexOf(selectedZone)
        onZoneChange(zones[(idx + 1) % zones.size])
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = BorderStroke(1.dp, palette.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(palette.skyCyan))
                    Box(Modifier.width(2.dp).height(20.dp).background(palette.borderStrong))
                    Box(Modifier.size(10.dp).clip(CircleShape).background(palette.solarOrange))
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        Text(strings.networkAnalyzed, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = when (selectedZone) {
                                "Agglomeration" -> strings.zoneUrbanFull
                                "Hors_agglomeration" -> strings.zoneRuralFull
                                else -> strings.zoneNationalFull
                            },
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    Column {
                        Text(strings.narsaYearLabel, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${strings.yearStatPrefix} $selectedYear",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.solarOrange
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(palette.solarOrange)
                        .clickable { nextZone() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = strings.networkAnalyzed,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            OncfSegmentedBar(
                options = listOf("Total" to strings.zoneNational, "Agglomeration" to strings.zoneUrban, "Hors_agglomeration" to strings.zoneRural),
                selected = selectedZone,
                onSelect = onZoneChange
            )

            OncfSegmentedBar(
                options = listOf(2021 to "2021", 2022 to "2022", 2023 to "2023", 2024 to "2024"),
                selected = selectedYear,
                onSelect = onYearChange
            )
        }
    }
}

@Composable
fun PerforatedTicketCard(
    headerTag: String,
    accentColor: Color,
    stubLeft: String,
    stubRight: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalOncfPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted.copy(alpha = 0.55f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(accentColor))
                    Text(
                        text = headerTag,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.inkPrimary
                    )
                }
                Text(
                    text = "NARSA • MA",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    color = palette.inkMuted
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                content = content
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val cy = size.height / 2f
                    val notchR = 10.dp.toPx()
                    drawLine(
                        color = palette.borderStrong,
                        start = Offset(notchR + 6f, cy),
                        end = Offset(size.width - notchR - 6f, cy),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                    drawCircle(color = palette.canvas, radius = notchR, center = Offset(0f, cy))
                    drawCircle(color = palette.border, radius = notchR, center = Offset(0f, cy), style = Stroke(1.5f))
                    drawCircle(color = palette.canvas, radius = notchR, center = Offset(size.width, cy))
                    drawCircle(color = palette.border, radius = notchR, center = Offset(size.width, cy), style = Stroke(1.5f))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stubLeft, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = palette.inkSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(stubRight, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
                Canvas(modifier = Modifier.width(64.dp).height(20.dp)) {
                    val widths = listOf(2f, 4f, 1.5f, 3f, 5f, 2f, 3.5f, 1.5f, 4f, 2f)
                    var bx = 0f
                    widths.forEachIndexed { idx, w ->
                        if (idx % 2 == 0 || w > 2f) {
                            drawRect(palette.inkMuted.copy(alpha = 0.5f), Offset(bx, 2f), Size(w, size.height - 4f))
                        }
                        bx += w + 2.5f
                    }
                }
            }
        }
    }
}

@Composable
fun SpeedometerArcGauge(
    title: String,
    valueText: String,
    subtitle: String,
    fraction: Float,
    arcColor: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalOncfPalette.current
    val sweepAnim = remember { Animatable(0f) }
    LaunchedEffect(fraction) {
        sweepAnim.animateTo(fraction.coerceIn(0f, 1f), tween(850, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(92.dp)) {
            Canvas(modifier = Modifier.size(86.dp)) {
                val strokeW = 10.dp.toPx()
                drawArc(
                    color = palette.surfaceMuted,
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                drawArc(
                    color = arcColor,
                    startAngle = 150f,
                    sweepAngle = 240f * sweepAnim.value,
                    useCenter = false,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                val rOuter = size.minDimension / 2f - strokeW * 0.9f
                val cx = size.width / 2f
                val cy = size.height / 2f
                for (i in 0..8) {
                    val deg = 150f + (240f / 8f) * i
                    val rad = (deg * PI / 180.0).toFloat()
                    drawLine(
                        color = palette.borderStrong,
                        start = Offset(cx + cos(rad) * (rOuter - 5f), cy + sin(rad) * (rOuter - 5f)),
                        end = Offset(cx + cos(rad) * (rOuter - 1f), cy + sin(rad) * (rOuter - 1f)),
                        strokeWidth = 2f
                    )
                }
            }
            Text(
                text = valueText,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = palette.inkPrimary
            )
        }
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.inkPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(subtitle, fontSize = 11.sp, color = palette.inkMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun <T> OncfSegmentedBar(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalOncfPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (id, label) ->
            val isSel = id == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (isSel) palette.navyPrimary else Color.Transparent)
                    .clickable { onSelect(id) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSel) Color.White else palette.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

data class ChartSeriesPoint(
    val label: String,
    val valueA: Float,
    val valueB: Float
)

@Composable
fun InteractiveSplineChart(
    data: List<ChartSeriesPoint>,
    labelA: String,
    colorA: Color,
    labelB: String,
    colorB: Color,
    valueFormatter: (Float) -> String = { formatNumberFr(it.roundToInt()) },
    modifier: Modifier = Modifier
) {
    val palette = LocalOncfPalette.current
    var selectedIdx by remember(data) { mutableIntStateOf((data.size - 1).coerceAtLeast(0)) }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(data) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(750, easing = FastOutSlowInEasing))
    }
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChartLegendItem(labelA, colorA)
                ChartLegendItem(labelB, colorB)
            }
            if (data.isNotEmpty() && selectedIdx in data.indices) {
                val pt = data[selectedIdx]
                Surface(color = palette.navyPrimary, shape = RoundedCornerShape(999.dp)) {
                    Text(
                        text = "${pt.label} • ${valueFormatter(pt.valueA)} | ${valueFormatter(pt.valueB)}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        if (data.size > 1) {
                            val pad = 22f
                            val chartW = (size.width - pad * 2f).coerceAtLeast(1f)
                            val frac = ((offset.x - pad) / chartW).coerceIn(0f, 1f)
                            selectedIdx = (frac * (data.size - 1)).roundToInt().coerceIn(0, data.lastIndex)
                        }
                    }
                }
                .pointerInput(data) {
                    detectHorizontalDragGestures { change, _ ->
                        if (data.size > 1) {
                            val pad = 22f
                            val chartW = (size.width - pad * 2f).coerceAtLeast(1f)
                            val frac = ((change.position.x - pad) / chartW).coerceIn(0f, 1f)
                            selectedIdx = (frac * (data.size - 1)).roundToInt().coerceIn(0, data.lastIndex)
                        }
                    }
                }
        ) {
            if (data.isEmpty()) return@Canvas
            val maxVal = data.maxOf { maxOf(it.valueA, it.valueB) }.coerceAtLeast(1f) * 1.15f
            val padLeft = 22f
            val padRight = 22f
            val padTop = 18f
            val padBottom = 26f
            val chartW = size.width - padLeft - padRight
            val chartH = size.height - padTop - padBottom

            for (g in 0..3) {
                val y = padTop + chartH * (g / 3f)
                drawLine(palette.border, Offset(padLeft, y), Offset(size.width - padRight, y), strokeWidth = 1.2f)
            }

            fun xFor(idx: Int) = if (data.size == 1) padLeft + chartW / 2f else padLeft + (idx.toFloat() / (data.size - 1)) * chartW
            fun yFor(v: Float) = padTop + chartH - (v / maxVal) * chartH * anim.value

            val ptsA = data.mapIndexed { i, p -> Offset(xFor(i), yFor(p.valueA)) }
            val ptsB = data.mapIndexed { i, p -> Offset(xFor(i), yFor(p.valueB)) }

            if (selectedIdx in data.indices) {
                val sx = xFor(selectedIdx)
                drawLine(
                    color = palette.solarOrange.copy(alpha = 0.55f),
                    start = Offset(sx, padTop),
                    end = Offset(sx, padTop + chartH),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            }

            fun buildSmoothPath(pts: List<Offset>): Path {
                val path = Path()
                if (pts.isEmpty()) return path
                path.moveTo(pts.first().x, pts.first().y)
                for (i in 0 until pts.lastIndex) {
                    val p0 = pts[i]
                    val p1 = pts[i + 1]
                    val midX = (p0.x + p1.x) / 2f
                    path.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                }
                return path
            }

            if (ptsA.size > 1) {
                val splineA = buildSmoothPath(ptsA)
                val fillA = Path().apply {
                    addPath(splineA)
                    lineTo(ptsA.last().x, padTop + chartH)
                    lineTo(ptsA.first().x, padTop + chartH)
                    close()
                }
                drawPath(
                    fillA,
                    Brush.verticalGradient(listOf(colorA.copy(alpha = 0.24f), Color.Transparent), padTop, padTop + chartH)
                )
                drawPath(splineA, colorA, style = Stroke(5f, cap = StrokeCap.Round))

                val splineB = buildSmoothPath(ptsB)
                val fillB = Path().apply {
                    addPath(splineB)
                    lineTo(ptsB.last().x, padTop + chartH)
                    lineTo(ptsB.first().x, padTop + chartH)
                    close()
                }
                drawPath(
                    fillB,
                    Brush.verticalGradient(listOf(colorB.copy(alpha = 0.20f), Color.Transparent), padTop, padTop + chartH)
                )
                drawPath(splineB, colorB, style = Stroke(5f, cap = StrokeCap.Round))
            }

            ptsA.forEachIndexed { idx, pt ->
                val isSel = idx == selectedIdx
                drawCircle(palette.surface, radius = if (isSel) 8f else 5f, center = pt)
                drawCircle(colorA, radius = if (isSel) 5.5f else 3.5f, center = pt)
            }
            ptsB.forEachIndexed { idx, pt ->
                val isSel = idx == selectedIdx
                drawCircle(palette.surface, radius = if (isSel) 8f else 5f, center = pt)
                drawCircle(colorB, radius = if (isSel) 5.5f else 3.5f, center = pt)

                val xLbl = textMeasurer.measure(
                    data[idx].label,
                    TextStyle(
                        color = if (isSel) palette.inkPrimary else palette.inkMuted,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                )
                drawText(
                    xLbl,
                    topLeft = Offset((pt.x - xLbl.size.width / 2f).coerceIn(0f, size.width - xLbl.size.width), size.height - padBottom + 5f)
                )
            }
        }
    }
}

@Composable
fun InteractiveGroupedChart(
    data: List<ChartSeriesPoint>,
    labelA: String,
    colorA: Color,
    labelB: String,
    colorB: Color,
    valueFormatter: (Float) -> String = { formatNumberFr(it.roundToInt()) },
    modifier: Modifier = Modifier
) {
    val palette = LocalOncfPalette.current
    var selectedIdx by remember(data) { mutableIntStateOf((data.size - 1).coerceAtLeast(0)) }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(data) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
    }
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChartLegendItem(labelA, colorA)
                ChartLegendItem(labelB, colorB)
            }
            if (data.isNotEmpty() && selectedIdx in data.indices) {
                val pt = data[selectedIdx]
                Surface(color = palette.navyPrimary, shape = RoundedCornerShape(999.dp)) {
                    Text(
                        text = "${pt.label} • ${valueFormatter(pt.valueA)} | ${valueFormatter(pt.valueB)}",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        if (data.isNotEmpty()) {
                            val groupW = size.width.toFloat() / data.size
                            selectedIdx = (offset.x / groupW).toInt().coerceIn(0, data.lastIndex)
                        }
                    }
                }
        ) {
            if (data.isEmpty()) return@Canvas
            val maxVal = data.maxOf { maxOf(it.valueA, it.valueB) }.coerceAtLeast(1f)
            val padTop = 14f
            val padBottom = 26f
            val chartH = size.height - padTop - padBottom
            val groupW = size.width / data.size
            val barW = (groupW * 0.28f).coerceAtMost(32f)
            val gap = 6f

            for (g in 0..3) {
                val y = padTop + chartH * (g / 3f)
                drawLine(palette.border, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.2f)
            }

            data.forEachIndexed { idx, pt ->
                val cx = groupW * idx + groupW / 2f
                val isSel = idx == selectedIdx
                if (isSel) {
                    drawRoundRect(
                        color = palette.surfaceMuted,
                        topLeft = Offset(groupW * idx + 4f, padTop - 4f),
                        size = Size(groupW - 8f, chartH + 8f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }
                val hA = (pt.valueA / maxVal) * chartH * anim.value
                val hB = (pt.valueB / maxVal) * chartH * anim.value

                drawRoundRect(
                    color = if (isSel) colorA else colorA.copy(alpha = 0.72f),
                    topLeft = Offset(cx - barW - gap / 2f, padTop + chartH - hA),
                    size = Size(barW, hA),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = if (isSel) colorB else colorB.copy(alpha = 0.72f),
                    topLeft = Offset(cx + gap / 2f, padTop + chartH - hB),
                    size = Size(barW, hB),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                val lbl = textMeasurer.measure(
                    pt.label,
                    TextStyle(
                        color = if (isSel) palette.inkPrimary else palette.inkMuted,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                )
                drawText(lbl, topLeft = Offset(cx - lbl.size.width / 2f, size.height - padBottom + 5f))
            }
        }
    }
}

@Composable
private fun ChartLegendItem(label: String, color: Color) {
    val palette = LocalOncfPalette.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, fontSize = 11.5.sp, color = palette.inkSecondary, fontWeight = FontWeight.Medium)
    }
}
