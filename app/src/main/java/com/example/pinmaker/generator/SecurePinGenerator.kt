package com.example.pinmaker.generator

import com.example.pinmaker.model.GenerationResult
import com.example.pinmaker.model.PinConfig
import java.security.SecureRandom
import kotlin.math.abs

/**
 * Cryptographically secure PIN generator implementation.
 *
 * Employs [SecureRandom] exclusively for all random choices, with zero modulo bias,
 * strict validation of all constraints, and randomized backtracking to handle complex
 * constraint intersections.
 */
class SecurePinGenerator(
    private val secureRandom: SecureRandom = SecureRandom()
) : PinGenerator {

    override fun generate(config: PinConfig): GenerationResult {
        // 1. Length validation
        if (config.length < PinConfig.MIN_LENGTH || config.length > PinConfig.MAX_LENGTH) {
            return GenerationResult.Error(
                "PIN length must be between ${PinConfig.MIN_LENGTH} and ${PinConfig.MAX_LENGTH} digits."
            )
        }

        // 2. Prefix & suffix characters validation
        if (config.prefix.any { !it.isDigit() }) {
            return GenerationResult.Error("Prefix must contain numeric digits (0-9) only.")
        }
        if (config.suffix.any { !it.isDigit() }) {
            return GenerationResult.Error("Suffix must contain numeric digits (0-9) only.")
        }

        // 3. Combined length check
        val fixedLength = config.prefix.length + config.suffix.length
        if (fixedLength >= config.length) {
            return GenerationResult.Error(
                "Combined prefix and suffix length ($fixedLength) must be less than the total PIN length (${config.length})."
            )
        }

        // 4. Repeated digits validation
        if (config.preventRepeatedDigits) {
            if (config.length > 10) {
                return GenerationResult.Error(
                    "Cannot generate a PIN with more than 10 unique digits (only digits 0-9 exist)."
                )
            }
            if (config.prefix.toSet().size != config.prefix.length) {
                return GenerationResult.Error("Prefix contains repeated digits.")
            }
            if (config.suffix.toSet().size != config.suffix.length) {
                return GenerationResult.Error("Suffix contains repeated digits.")
            }
            val overlap = config.prefix.toSet().intersect(config.suffix.toSet())
            if (overlap.isNotEmpty()) {
                return GenerationResult.Error("Prefix and suffix share overlapping digits: ${overlap.joinToString()}.")
            }
        }

        // 5. Consecutive digits validation for prefix & suffix internals
        if (config.preventConsecutiveDigits) {
            for (i in 0 until config.prefix.length - 1) {
                val a = config.prefix[i].digitToInt()
                val b = config.prefix[i + 1].digitToInt()
                if (a == b || abs(a - b) == 1) {
                    return GenerationResult.Error("Prefix contains consecutive or identical adjacent digits ($a$b).")
                }
            }
            for (i in 0 until config.suffix.length - 1) {
                val a = config.suffix[i].digitToInt()
                val b = config.suffix[i + 1].digitToInt()
                if (a == b || abs(a - b) == 1) {
                    return GenerationResult.Error("Suffix contains consecutive or identical adjacent digits ($a$b).")
                }
            }
        }

        // 6. Generate the random digits using randomized backtracking
        val pinDigits = IntArray(config.length)

        // Pre-fill prefix
        for (i in config.prefix.indices) {
            pinDigits[i] = config.prefix[i].digitToInt()
        }

        // Pre-fill suffix
        val suffixStartIndex = config.length - config.suffix.length
        for (i in config.suffix.indices) {
            pinDigits[suffixStartIndex + i] = config.suffix[i].digitToInt()
        }

        val usedDigits = BooleanArray(10)
        if (config.preventRepeatedDigits) {
            for (i in 0 until config.prefix.length) {
                usedDigits[pinDigits[i]] = true
            }
            for (i in suffixStartIndex until config.length) {
                usedDigits[pinDigits[i]] = true
            }
        }

        val startIndex = config.prefix.length
        val endIndex = suffixStartIndex // exclusive

        val success = fillDigits(
            digits = pinDigits,
            currentIndex = startIndex,
            endIndex = endIndex,
            usedDigits = usedDigits,
            config = config,
            maxSteps = 2000,
            stepCount = IntArray(1)
        )

        return if (success) {
            val sb = StringBuilder(config.length)
            for (d in pinDigits) {
                sb.append(d)
            }
            GenerationResult.Success(sb.toString())
        } else {
            GenerationResult.Error("Unable to satisfy all constraints simultaneously. Try loosening restrictions.")
        }
    }

    private fun fillDigits(
        digits: IntArray,
        currentIndex: Int,
        endIndex: Int,
        usedDigits: BooleanArray,
        config: PinConfig,
        maxSteps: Int,
        stepCount: IntArray
    ): Boolean {
        if (currentIndex == endIndex) {
            return true
        }

        if (stepCount[0]++ > maxSteps) {
            return false
        }

        // Determine all valid candidates for this position
        val candidates = ArrayList<Int>(10)
        for (digit in 0..9) {
            if (config.preventRepeatedDigits && usedDigits[digit]) {
                continue
            }

            if (config.preventConsecutiveDigits) {
                // Check previous digit if applicable
                if (currentIndex > 0) {
                    val prev = digits[currentIndex - 1]
                    if (digit == prev || abs(digit - prev) == 1) {
                        continue
                    }
                }

                // Check next digit if this is the last random digit and suffix is present
                if (currentIndex == endIndex - 1 && config.suffix.isNotEmpty()) {
                    val next = digits[endIndex]
                    if (digit == next || abs(digit - next) == 1) {
                        continue
                    }
                }
            }

            candidates.add(digit)
        }

        if (candidates.isEmpty()) {
            return false
        }

        // Fisher-Yates shuffle candidates using SecureRandom to ensure uniform random exploration
        for (i in candidates.size - 1 downTo 1) {
            val j = secureRandom.nextInt(i + 1)
            val temp = candidates[i]
            candidates[i] = candidates[j]
            candidates[j] = temp
        }

        // Try candidates in cryptographically randomized order
        for (candidate in candidates) {
            digits[currentIndex] = candidate
            if (config.preventRepeatedDigits) {
                usedDigits[candidate] = true
            }

            if (fillDigits(digits, currentIndex + 1, endIndex, usedDigits, config, maxSteps, stepCount)) {
                return true
            }

            // Backtrack
            if (config.preventRepeatedDigits) {
                usedDigits[candidate] = false
            }
        }

        return false
    }
}
