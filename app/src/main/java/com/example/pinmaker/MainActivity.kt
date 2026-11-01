package com.example.pinmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pinmaker.data.UserPreferencesRepository
import com.example.pinmaker.generator.SecurePinGenerator
import com.example.pinmaker.ui.navigation.PinNavGraph
import com.example.pinmaker.ui.theme.PinMakerTheme
import com.example.pinmaker.ui.viewmodel.PinViewModel
import com.example.pinmaker.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val preferencesRepository by lazy {
        UserPreferencesRepository(applicationContext)
    }

    private val pinViewModel: PinViewModel by viewModels {
        PinViewModel.Factory(
            generator = SecurePinGenerator(),
            preferencesRepository = preferencesRepository
        )
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(
            preferencesRepository = preferencesRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()

            PinMakerTheme(appTheme = preferences.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PinNavGraph(
                        pinViewModel = pinViewModel,
                        settingsViewModel = settingsViewModel,
                        userPreferences = preferences
                    )
                }
            }
        }
    }
}
