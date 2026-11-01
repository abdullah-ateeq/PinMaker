package com.example.pinmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pinmaker.data.UserPreferences
import com.example.pinmaker.data.UserPreferencesRepository
import com.example.pinmaker.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }

    fun updateDefaultLength(length: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultLength(length)
        }
    }

    fun updateDefaultPreventRepeated(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultPreventRepeated(enabled)
        }
    }

    fun updateDefaultPreventConsecutive(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultPreventConsecutive(enabled)
        }
    }

    fun updateShowPinByDefault(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateShowPinByDefault(enabled)
        }
    }

    fun updateHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHapticFeedbackEnabled(enabled)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            preferencesRepository.resetToDefaults()
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
