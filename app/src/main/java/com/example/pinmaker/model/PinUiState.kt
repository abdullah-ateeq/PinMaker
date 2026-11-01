package com.example.pinmaker.model

/**
 * State representing the Home Screen UI for PIN generation.
 */
data class PinUiState(
    val pin: String = "",
    val isPinVisible: Boolean = true,
    val length: Int = PinConfig.DEFAULT_LENGTH,
    val preventRepeatedDigits: Boolean = false,
    val preventConsecutiveDigits: Boolean = false,
    val prefix: String = "",
    val suffix: String = "",
    val errorMessage: String? = null,
    val isRegenerating: Boolean = false,
    val isInitialized: Boolean = false
)
