package com.example.pinmaker.generator

import com.example.pinmaker.model.GenerationResult
import com.example.pinmaker.model.PinConfig

/**
 * Interface defining the PIN generation contract.
 */
interface PinGenerator {
    /**
     * Generates a PIN based on the provided configuration.
     *
     * @param config The [PinConfig] specifying length, restrictions, prefix, and suffix.
     * @return [GenerationResult.Success] with the generated PIN or [GenerationResult.Error] with the failure reason.
     */
    fun generate(config: PinConfig): GenerationResult
}
