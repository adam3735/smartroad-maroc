package ma.narsa.smartroad

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.narsa.smartroad.ui.components.MotionOpening
import ma.narsa.smartroad.ui.components.OncfBrandMark
import ma.narsa.smartroad.ui.i18n.AppLanguage
import ma.narsa.smartroad.ui.i18n.ArabicStrings
import ma.narsa.smartroad.ui.i18n.FrenchStrings
import ma.narsa.smartroad.ui.i18n.LocalAppLanguage
import ma.narsa.smartroad.ui.i18n.LocalAppStrings
import ma.narsa.smartroad.ui.screens.AiLabScreen
import ma.narsa.smartroad.ui.screens.ExplorerScreen
import ma.narsa.smartroad.ui.screens.OverviewScreen
import ma.narsa.smartroad.ui.screens.RatiosScreen
import ma.narsa.smartroad.ui.screens.SimulatorScreen
import ma.narsa.smartroad.ui.theme.LocalOncfPalette
import ma.narsa.smartroad.ui.theme.OncfTheme

data class OncfDestination(
    val id: String,
    val label: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isNight by remember { mutableStateOf(false) }
            var language by remember { mutableStateOf(AppLanguage.FR) }
            var showOpening by remember { mutableStateOf(true) }

            val strings = if (language == AppLanguage.AR) ArabicStrings else FrenchStrings
            val layoutDir = if (language == AppLanguage.AR) LayoutDirection.Rtl else LayoutDirection.Ltr

            OncfTheme(isNight = isNight) {
                CompositionLocalProvider(
                    LocalAppLanguage provides language,
                    LocalAppStrings provides strings,
                    LocalLayoutDirection provides layoutDir
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SmartRoadShell(
                            isNight = isNight,
                            onToggleNight = { isNight = !isNight },
                            onToggleLanguage = {
                                language = if (language == AppLanguage.FR) AppLanguage.AR else AppLanguage.FR
                            }
                        )
                        AnimatedVisibility(
                            visible = showOpening,
                            enter = fadeIn(tween(120)),
                            exit = fadeOut(tween(420))
                        ) {
                            MotionOpening(onComplete = { showOpening = false })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartRoadShell(
    isNight: Boolean,
    onToggleNight: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    val palette = LocalOncfPalette.current
    val strings = LocalAppStrings.current
    var activeDest by remember { mutableStateOf("accueil") }
    val snackbarHostState = remember { SnackbarHostState() }

    val destinations = remember(strings) {
        listOf(
            OncfDestination("accueil", strings.navOverview, Icons.Default.Explore),
            OncfDestination("dashboard", strings.navExplorer, Icons.Default.Assessment),
            OncfDestination("comparaison", strings.navCockpit, Icons.Default.Speed),
            OncfDestination("analyse", strings.navRatios, Icons.Default.BarChart),
            OncfDestination("ia", strings.navAi, Icons.Default.Psychology)
        )
    }

    val config = LocalConfiguration.current
    val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.canvas)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val useSideRail = maxWidth >= 600.dp || isLandscape
        val isWideContent = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useSideRail) {
                NavigationRail(
                    containerColor = palette.navyPrimary,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(Modifier.height(12.dp))
                    OncfBrandMark(size = 34.dp)
                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        destinations.forEach { dest ->
                            NavigationRailItem(
                                selected = activeDest == dest.id,
                                onClick = { activeDest = dest.id },
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label, fontSize = 11.sp, maxLines = 1) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = palette.solarOrange,
                                    indicatorColor = palette.solarOrange,
                                    unselectedIconColor = Color.White.copy(alpha = 0.65f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.65f)
                                )
                            )
                        }
                    }
                }
            }

            Scaffold(
                containerColor = palette.canvas,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (!useSideRail) {
                        FloatingOncfBottomDock(
                            destinations = destinations,
                            activeId = activeDest,
                            onSelect = { activeDest = it }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = activeDest,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(210)) togetherWith fadeOut(animationSpec = tween(150))
                        },
                        label = "oncf_screen_switch"
                    ) { target ->
                        when (target) {
                            "accueil" -> OverviewScreen(
                                isWide = isWideContent,
                                isNight = isNight,
                                onToggleNight = onToggleNight,
                                onToggleLanguage = onToggleLanguage,
                                onNavigate = { activeDest = it }
                            )
                            "dashboard" -> ExplorerScreen(
                                isWide = isWideContent,
                                isNight = isNight,
                                onToggleNight = onToggleNight,
                                onToggleLanguage = onToggleLanguage,
                                snackbarHostState = snackbarHostState
                            )
                            "comparaison" -> SimulatorScreen(
                                isWide = isWideContent,
                                isNight = isNight,
                                onToggleNight = onToggleNight,
                                onToggleLanguage = onToggleLanguage
                            )
                            "analyse" -> RatiosScreen(
                                isWide = isWideContent,
                                isNight = isNight,
                                onToggleNight = onToggleNight,
                                onToggleLanguage = onToggleLanguage
                            )
                            "ia" -> AiLabScreen(
                                isWide = isWideContent,
                                isNight = isNight,
                                onToggleNight = onToggleNight,
                                onToggleLanguage = onToggleLanguage
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingOncfBottomDock(
    destinations: List<OncfDestination>,
    activeId: String,
    onSelect: (String) -> Unit
) {
    val palette = LocalOncfPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = palette.navyPrimary,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { dest ->
                    val selected = dest.id == activeId
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (selected) palette.solarOrange else Color.Transparent)
                            .clickable { onSelect(dest.id) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = dest.label,
                            tint = if (selected) Color.White else Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = dest.label,
                            fontSize = 10.5.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) Color.White else Color.White.copy(alpha = 0.65f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
