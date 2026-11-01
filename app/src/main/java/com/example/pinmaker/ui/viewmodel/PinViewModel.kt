package com.example.pinmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pinmaker.data.UserPreferencesRepository
import com.example.pinmaker.generator.PinGenerator
import com.example.pinmaker.generator.SecurePinGenerator
import com.example.pinmaker.model.GenerationResult
import com.example.pinmaker.model.PinConfig
import com.example.pinmaker.model.PinUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PinViewModel(
    private val generator: PinGenerator = SecurePinGenerator(),
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinUiState())
    val uiState: StateFlow<PinUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferencesFlow.first()
            _uiState.update { current ->
                current.copy(
                    length = prefs.defaultLength,
                    preventRepeatedDigits = prefs.defaultPreventRepeated,
                    preventConsecutiveDigits = prefs.defaultPreventConsecutive,
                    isPinVisible = prefs.showPinByDefault,
                    isInitialized = true
                )
            }
            generatePin()
        }
    }

    fun setLength(newLength: Int) {
        val clamped = newLength.coerceIn(PinConfig.MIN_LENGTH, PinConfig.MAX_LENGTH)
        _uiState.update { it.copy(length = clamped) }
        generatePin()
    }

    fun setPreventRepeatedDigits(prevent: Boolean) {
        _uiState.update { it.copy(preventRepeatedDigits = prevent) }
        generatePin()
    }

    fun setPreventConsecutiveDigits(prevent: Boolean) {
        _uiState.update { it.copy(preventConsecutiveDigits = prevent) }
        generatePin()
    }

    fun setPrefix(newPrefix: String) {
        val sanitized = newPrefix.filter { it.isDigit() }
        _uiState.update { it.copy(prefix = sanitized) }
        generatePin()
    }

    fun setSuffix(newSuffix: String) {
        val sanitized = newSuffix.filter { it.isDigit() }
        _uiState.update { it.copy(suffix = sanitized) }
        generatePin()
    }

    fun togglePinVisibility() {
        _uiState.update { it.copy(isPinVisible = !it.isPinVisible) }
    }

    fun generatePin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegenerating = true) }

            val currentState = _uiState.value
            val config = PinConfig(
                length = currentState.length,
                preventRepeatedDigits = currentState.preventRepeatedDigits,
                preventConsecutiveDigits = currentState.preventConsecutiveDigits,
                prefix = currentState.prefix,
                suffix = currentState.suffix
            )

            // Small delay for smooth tactile UI feel when explicitly clicked
            delay(50)

            when (val result = generator.generate(config)) {
                is GenerationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            pin = result.pin,
                            errorMessage = null,
                            isRegenerating = false
                        )
                    }
                }
                is GenerationResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message,
                            isRegenerating = false
                        )
                    }
                }
            }
        }
    }

    class Factory(
        private val generator: PinGenerator,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PinViewModel::class.java)) {
                return PinViewModel(generator, preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
