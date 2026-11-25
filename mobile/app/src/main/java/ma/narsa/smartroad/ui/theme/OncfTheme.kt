package ma.narsa.smartroad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class OncfPalette(
    val isNight: Boolean,
    val navyPrimary: Color,
    val navyDeep: Color,
    val navyMid: Color,
    val solarOrange: Color,
    val solarOrangeSoft: Color,
    val solarOrangeDeep: Color,
    val skyCyan: Color,
    val skyCyanBright: Color,
    val skyCyanSoft: Color,
    val royalViolet: Color,
    val royalVioletSoft: Color,
    val amberSignal: Color,
    val coralAlert: Color,
    val coralAlertSoft: Color,
    val canvas: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val border: Color,
    val borderStrong: Color,
    val roadAsphalt: Color,
    val inkPrimary: Color,
    val inkSecondary: Color,
    val inkMuted: Color
)

val OncfDaylightPalette = OncfPalette(
    isNight = false,
    navyPrimary = Color(0xFF14213D),
    navyDeep = Color(0xFF0D172A),
    navyMid = Color(0xFF1E335C),
    solarOrange = Color(0xFFFF6B00),
    solarOrangeSoft = Color(0xFFFFF0E5),
    solarOrangeDeep = Color(0xFFCC5200),
    skyCyan = Color(0xFF0284C7),
    skyCyanBright = Color(0xFF38BDF8),
    skyCyanSoft = Color(0xFFE0F2FE),
    royalViolet = Color(0xFF6366F1),
    royalVioletSoft = Color(0xFFEEF2FF),
    amberSignal = Color(0xFFF59E0B),
    coralAlert = Color(0xFFF43F5E),
    coralAlertSoft = Color(0xFFFFE4E6),
    canvas = Color(0xFFF2F5FA),
    surface = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFE8EEF6),
    border = Color(0xFFD8E1EE),
    borderStrong = Color(0xFFBCC9DC),
    roadAsphalt = Color(0xFF16223B),
    inkPrimary = Color(0xFF0F172A),
    inkSecondary = Color(0xFF475569),
    inkMuted = Color(0xFF64748B)
)

val OncfNightBoraqPalette = OncfPalette(
    isNight = true,
    navyPrimary = Color(0xFF0E172C),
    navyDeep = Color(0xFF070D1B),
    navyMid = Color(0xFF19284A),
    solarOrange = Color(0xFFFF7A1A),
    solarOrangeSoft = Color(0xFF382010),
    solarOrangeDeep = Color(0xFFFF9E57),
    skyCyan = Color(0xFF38BDF8),
    skyCyanBright = Color(0xFF7DD3FC),
    skyCyanSoft = Color(0xFF102C44),
    royalViolet = Color(0xFF818CF8),
    royalVioletSoft = Color(0xFF1F2347),
    amberSignal = Color(0xFFFBBF24),
    coralAlert = Color(0xFFFB7185),
    coralAlertSoft = Color(0xFF3B1522),
    canvas = Color(0xFF0B1221),
    surface = Color(0xFF131E33),
    surfaceMuted = Color(0xFF1A2842),
    border = Color(0xFF263859),
    borderStrong = Color(0xFF364F7D),
    roadAsphalt = Color(0xFF0D1526),
    inkPrimary = Color(0xFFF8FAFC),
    inkSecondary = Color(0xFFCBD5E1),
    inkMuted = Color(0xFF94A3B8)
)

val LocalOncfPalette = staticCompositionLocalOf { OncfDaylightPalette }

private val DaylightScheme = lightColorScheme(
    primary = OncfDaylightPalette.solarOrange,
    onPrimary = Color.White,
    primaryContainer = OncfDaylightPalette.solarOrangeSoft,
    onPrimaryContainer = OncfDaylightPalette.solarOrangeDeep,
    secondary = OncfDaylightPalette.skyCyan,
    onSecondary = Color.White,
    secondaryContainer = OncfDaylightPalette.skyCyanSoft,
    onSecondaryContainer = OncfDaylightPalette.navyPrimary,
    background = OncfDaylightPalette.canvas,
    onBackground = OncfDaylightPalette.inkPrimary,
    surface = OncfDaylightPalette.surface,
    onSurface = OncfDaylightPalette.inkPrimary,
    surfaceVariant = OncfDaylightPalette.surfaceMuted,
    onSurfaceVariant = OncfDaylightPalette.inkSecondary,
    outline = OncfDaylightPalette.border,
    outlineVariant = OncfDaylightPalette.borderStrong
)

private val NightScheme = darkColorScheme(
    primary = OncfNightBoraqPalette.solarOrange,
    onPrimary = Color(0xFF1F0C00),
    primaryContainer = OncfNightBoraqPalette.solarOrangeSoft,
    onPrimaryContainer = OncfNightBoraqPalette.solarOrangeDeep,
    secondary = OncfNightBoraqPalette.skyCyan,
    onSecondary = Color(0xFF031F30),
    secondaryContainer = OncfNightBoraqPalette.skyCyanSoft,
    onSecondaryContainer = OncfNightBoraqPalette.skyCyanBright,
    background = OncfNightBoraqPalette.canvas,
    onBackground = OncfNightBoraqPalette.inkPrimary,
    surface = OncfNightBoraqPalette.surface,
    onSurface = OncfNightBoraqPalette.inkPrimary,
    surfaceVariant = OncfNightBoraqPalette.surfaceMuted,
    onSurfaceVariant = OncfNightBoraqPalette.inkSecondary,
    outline = OncfNightBoraqPalette.border,
    outlineVariant = OncfNightBoraqPalette.borderStrong
)

private val OncfTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.3).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.5.sp,
        lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.5.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.5.sp,
        lineHeight = 15.sp
    )
)

@Composable
fun OncfTheme(
    isNight: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = if (isNight) OncfNightBoraqPalette else OncfDaylightPalette
    val scheme = if (isNight) NightScheme else DaylightScheme

    CompositionLocalProvider(LocalOncfPalette provides palette) {
        MaterialTheme(
            colorScheme = scheme,
            typography = OncfTypography,
            content = content
        )
    }
}
