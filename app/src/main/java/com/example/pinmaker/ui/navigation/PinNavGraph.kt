package com.example.pinmaker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pinmaker.data.UserPreferences
import com.example.pinmaker.ui.screens.about.AboutScreen
import com.example.pinmaker.ui.screens.home.HomeScreen
import com.example.pinmaker.ui.screens.settings.SettingsScreen
import com.example.pinmaker.ui.screens.splash.SplashScreen
import com.example.pinmaker.ui.viewmodel.PinViewModel
import com.example.pinmaker.ui.viewmodel.SettingsViewModel

@Composable
fun PinNavGraph(
    pinViewModel: PinViewModel,
    settingsViewModel: SettingsViewModel,
    userPreferences: UserPreferences,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val uiState by pinViewModel.uiState.collectAsStateWithLifecycle()
    val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(220))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(220))
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                uiState = uiState,
                hapticEnabled = preferences.hapticFeedbackEnabled,
                onLengthChanged = pinViewModel::setLength,
                onToggleRepeatedDigits = pinViewModel::setPreventRepeatedDigits,
                onToggleConsecutiveDigits = pinViewModel::setPreventConsecutiveDigits,
                onPrefixChanged = pinViewModel::setPrefix,
                onSuffixChanged = pinViewModel::setSuffix,
                onToggleVisibility = pinViewModel::togglePinVisibility,
                onRegenerate = pinViewModel::generatePin,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                preferences = preferences,
                onThemeChanged = settingsViewModel::updateTheme,
                onDefaultLengthChanged = settingsViewModel::updateDefaultLength,
                onDefaultPreventRepeatedChanged = settingsViewModel::updateDefaultPreventRepeated,
                onDefaultPreventConsecutiveChanged = settingsViewModel::updateDefaultPreventConsecutive,
                onShowPinByDefaultChanged = settingsViewModel::updateShowPinByDefault,
                onHapticFeedbackChanged = settingsViewModel::updateHapticFeedbackEnabled,
                onResetDefaults = settingsViewModel::resetSettings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
