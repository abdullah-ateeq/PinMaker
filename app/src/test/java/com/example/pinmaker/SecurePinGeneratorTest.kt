package com.example.pinmaker

import com.example.pinmaker.generator.SecurePinGenerator
import com.example.pinmaker.model.GenerationResult
import com.example.pinmaker.model.PinConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class SecurePinGeneratorTest {

    private lateinit var generator: SecurePinGenerator

    @Before
    fun setUp() {
        generator = SecurePinGenerator()
    }

    @Test
    fun testPinLengthAndDigitValidity_acrossAllAllowedLengths() {
        for (len in PinConfig.MIN_LENGTH..PinConfig.MAX_LENGTH) {
            val config = PinConfig(length = len)
            val result = generator.generate(config)

            assertTrue("Result should be Success for length $len", result is GenerationResult.Success)
            val pin = (result as GenerationResult.Success).pin
            assertEquals("Length must match requested length $len", len, pin.length)
            assertTrue("All characters must be digits for length $len", pin.all { it.isDigit() })
        }
    }

    @Test
    fun testRepeatedDigitRestrictions_enforcesUniqueDigits() {
        for (len in 4..10) {
            val config = PinConfig(
                length = len,
                preventRepeatedDigits = true
            )
            val result = generator.generate(config)
            assertTrue(result is GenerationResult.Success)
            val pin = (result as GenerationResult.Success).pin

            assertEquals(len, pin.length)
            val uniqueDigits = pin.toSet()
            assertEquals("All digits must be unique when preventRepeatedDigits is true", len, uniqueDigits.size)
        }
    }

    @Test
    fun testConsecutiveDigitRestrictions_enforcesNoAdjacentSequencesOrDuplicates() {
        // Run multiple iterations to verify random selections comply with rule
        repeat(50) {
            val config = PinConfig(
                length = 8,
                preventConsecutiveDigits = true
            )
            val result = generator.generate(config)
            assertTrue(result is GenerationResult.Success)
            val pin = (result as GenerationResult.Success).pin

            for (i in 0 until pin.length - 1) {
                val d1 = pin[i].digitToInt()
                val d2 = pin[i + 1].digitToInt()
                assertNotEquals("Adjacent digits must not be identical ($d1$d2)", d1, d2)
                assertNotEquals("Adjacent digits must not be consecutive ($d1$d2)", 1, abs(d1 - d2))
            }
        }
    }

    @Test
    fun testCombinedRestrictions_repeatedAndConsecutiveDigits() {
        repeat(30) {
            val config = PinConfig(
                length = 7,
                preventRepeatedDigits = true,
                preventConsecutiveDigits = true
            )
            val result = generator.generate(config)
            assertTrue(result is GenerationResult.Success)
            val pin = (result as GenerationResult.Success).pin

            assertEquals(7, pin.length)
            assertEquals("All digits must be unique", 7, pin.toSet().size)

            for (i in 0 until pin.length - 1) {
                val d1 = pin[i].digitToInt()
                val d2 = pin[i + 1].digitToInt()
                assertNotEquals("Adjacent digits must not be identical ($d1$d2)", d1, d2)
                assertNotEquals("Adjacent digits must not be consecutive ($d1$d2)", 1, abs(d1 - d2))
            }
        }
    }

    @Test
    fun testPrefixAndSuffixApplication() {
        val config = PinConfig(
            length = 8,
            prefix = "48",
            suffix = "15"
        )
        val result = generator.generate(config)
        assertTrue(result is GenerationResult.Success)
        val pin = (result as GenerationResult.Success).pin

        assertEquals(8, pin.length)
        assertTrue("PIN must start with prefix", pin.startsWith("48"))
        assertTrue("PIN must end with suffix", pin.endsWith("15"))
    }

    @Test
    fun testPrefixOnlyAndSuffixOnly() {
        val prefixConfig = PinConfig(length = 6, prefix = "135")
        val prefixResult = generator.generate(prefixConfig)
        assertTrue(prefixResult is GenerationResult.Success)
        val prefixPin = (prefixResult as GenerationResult.Success).pin
        assertEquals(6, prefixPin.length)
        assertTrue(prefixPin.startsWith("135"))

        val suffixConfig = PinConfig(length = 6, suffix = "246")
        val suffixResult = generator.generate(suffixConfig)
        assertTrue(suffixResult is GenerationResult.Success)
        val suffixPin = (suffixResult as GenerationResult.Success).pin
        assertEquals(6, suffixPin.length)
        assertTrue(suffixPin.endsWith("246"))
    }

    @Test
    fun testPrefixSuffixWithConsecutiveRule() {
        val config = PinConfig(
            length = 8,
            prefix = "24",
            suffix = "68",
            preventConsecutiveDigits = true
        )
        val result = generator.generate(config)
        assertTrue(result is GenerationResult.Success)
        val pin = (result as GenerationResult.Success).pin

        assertTrue(pin.startsWith("24"))
        assertTrue(pin.endsWith("68"))

        for (i in 0 until pin.length - 1) {
            val d1 = pin[i].digitToInt()
            val d2 = pin[i + 1].digitToInt()
            assertNotEquals("Adjacent digits must not be identical ($d1$d2)", d1, d2)
            assertNotEquals("Adjacent digits must not be consecutive ($d1$d2)", 1, abs(d1 - d2))
        }
    }

    @Test
    fun testInvalidConfigurations_rejectedGracefully() {
        // Length < 4
        assertTrue(generator.generate(PinConfig(length = 3)) is GenerationResult.Error)

        // Length > 12
        assertTrue(generator.generate(PinConfig(length = 13)) is GenerationResult.Error)

        // Length > 10 with unique digits required
        assertTrue(generator.generate(PinConfig(length = 11, preventRepeatedDigits = true)) is GenerationResult.Error)

        // Non-digit prefix
        assertTrue(generator.generate(PinConfig(prefix = "12a")) is GenerationResult.Error)

        // Non-digit suffix
        assertTrue(generator.generate(PinConfig(suffix = "9#")) is GenerationResult.Error)

        // Prefix + suffix length >= total length
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "123", suffix = "456")) is GenerationResult.Error)
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "1234", suffix = "567")) is GenerationResult.Error)

        // Prefix contains repeated digits when preventRepeatedDigits is true
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "11", preventRepeatedDigits = true)) is GenerationResult.Error)

        // Suffix contains repeated digits when preventRepeatedDigits is true
        assertTrue(generator.generate(PinConfig(length = 6, suffix = "99", preventRepeatedDigits = true)) is GenerationResult.Error)

        // Prefix and suffix share digits when preventRepeatedDigits is true
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "13", suffix = "35", preventRepeatedDigits = true)) is GenerationResult.Error)

        // Prefix contains consecutive digits when preventConsecutiveDigits is true
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "12", preventConsecutiveDigits = true)) is GenerationResult.Error)
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "54", preventConsecutiveDigits = true)) is GenerationResult.Error)
        assertTrue(generator.generate(PinConfig(length = 6, prefix = "33", preventConsecutiveDigits = true)) is GenerationResult.Error)
    }

    @Test
    fun testRegenerationProducesFreshResults() {
        val config = PinConfig(length = 8)
        val pins = (1..50).map {
            val res = generator.generate(config)
            assertTrue(res is GenerationResult.Success)
            (res as GenerationResult.Success).pin
        }.toSet()

        // In 50 generations of 8-digit random PINs, entropy should yield high uniqueness
        assertTrue("Expected many unique PINs across 50 generations, got ${pins.size}", pins.size >= 45)
    }

    @Test
    fun testDigitDistribution_noModuloBias() {
        // Collect 5,000 digits from unconstrained generation to check all digits 0-9 appear with reasonable distribution
        val counts = IntArray(10)
        val config = PinConfig(length = 10)
        repeat(500) {
            val res = generator.generate(config) as GenerationResult.Success
            for (char in res.pin) {
                counts[char.digitToInt()]++
            }
        }

        // Each digit in 5,000 samples has expected value 500. Ensure no digit is starved or excessively biased.
        for (digit in 0..9) {
            val count = counts[digit]
            assertTrue("Digit $digit count $count should be close to uniform (within 350..650)", count in 350..650)
        }
    }
}
