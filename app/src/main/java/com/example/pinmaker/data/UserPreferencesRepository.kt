package com.example.pinmaker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pinmaker.model.AppTheme
import com.example.pinmaker.model.PinConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pinmaker_preferences")

data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val defaultLength: Int = PinConfig.DEFAULT_LENGTH,
    val defaultPreventRepeated: Boolean = false,
    val defaultPreventConsecutive: Boolean = false,
    val showPinByDefault: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("app_theme")
        val DEFAULT_LENGTH = intPreferencesKey("default_pin_length")
        val DEFAULT_PREVENT_REPEATED = booleanPreferencesKey("default_prevent_repeated")
        val DEFAULT_PREVENT_CONSECUTIVE = booleanPreferencesKey("default_prevent_consecutive")
        val SHOW_PIN_BY_DEFAULT = booleanPreferencesKey("show_pin_by_default")
        val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
            val theme = try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }

            val defaultLength = (preferences[PreferencesKeys.DEFAULT_LENGTH] ?: PinConfig.DEFAULT_LENGTH)
                .coerceIn(PinConfig.MIN_LENGTH, PinConfig.MAX_LENGTH)
            val preventRepeated = preferences[PreferencesKeys.DEFAULT_PREVENT_REPEATED] ?: false
            val preventConsecutive = preferences[PreferencesKeys.DEFAULT_PREVENT_CONSECUTIVE] ?: false
            val showPin = preferences[PreferencesKeys.SHOW_PIN_BY_DEFAULT] ?: true
            val haptic = preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] ?: true

            UserPreferences(
                theme = theme,
                defaultLength = defaultLength,
                defaultPreventRepeated = preventRepeated,
                defaultPreventConsecutive = preventConsecutive,
                showPinByDefault = showPin,
                hapticFeedbackEnabled = haptic
            )
        }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun updateDefaultLength(length: Int) {
        val clamped = length.coerceIn(PinConfig.MIN_LENGTH, PinConfig.MAX_LENGTH)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_LENGTH] = clamped
        }
    }

    suspend fun updateDefaultPreventRepeated(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PREVENT_REPEATED] = enabled
        }
    }

    suspend fun updateDefaultPreventConsecutive(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PREVENT_CONSECUTIVE] = enabled
        }
    }

    suspend fun updateShowPinByDefault(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_PIN_BY_DEFAULT] = enabled
        }
    }

    suspend fun updateHapticFeedbackEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK_ENABLED] = enabled
        }
    }

    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
