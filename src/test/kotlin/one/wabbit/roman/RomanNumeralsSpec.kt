package one.wabbit.roman

import kotlin.random.Random
import kotlin.test.*

class RomanNumeralsSpec {
    // --- Canonical table tests (STRICT expectations) ---
    @Test
    fun canonical_examples_strict() {
        val cases = mapOf(
            1 to "I",
            2 to "II",
            3 to "III",
            4 to "IV",
            5 to "V",
            9 to "IX",
            14 to "XIV",
            19 to "XIX",
            40 to "XL",
            44 to "XLIV",
            49 to "XLIX",
            90 to "XC",
            99 to "XCIX",
            400 to "CD",
            944 to "CMXLIV",
            2024 to "MMXXIV",
            3999 to "MMMCMXCIX"
        )
        for ((arabic, roman) in cases) {
            assertEquals(roman, RomanNumerals.toRoman(arabic, RomanMode.STRICT), "toRoman($arabic)")
            assertEquals(arabic, RomanNumerals.toArabic(roman, RomanMode.STRICT), "toArabic($roman)")
        }
    }

    // --- Extended behavior: allow 4000+ as "MMMM..." ---
    @Test
    fun extended_allows_4000_plus() {
        assertEquals("MMMM", RomanNumerals.toRoman(4000, RomanMode.EXTENDED))
        assertEquals(4000, RomanNumerals.toArabic("MMMM", RomanMode.EXTENDED))
        assertFalse(RomanNumerals.isValid("MMMM", RomanMode.STRICT))
    }

    // --- Round-trip properties ---
    @Test
    fun roundtrip_strict_1_to_3999() {
        for (i in 1..3999) {
            val r = RomanNumerals.toRoman(i, RomanMode.STRICT)
            assertTrue(RomanNumerals.isValid(r, RomanMode.STRICT), "non-canonical: $r")
            val back = RomanNumerals.toArabic(r, RomanMode.STRICT)
            assertEquals(i, back, "Failed for $i: $r -> $back")
        }
    }

    @Test
    fun roundtrip_extended_sampled() {
        val rng = Random(1234)
        repeat(2000) {
            val n = 1 + rng.nextInt(20_000) // keep strings small & tests fast
            val r = RomanNumerals.toRoman(n, RomanMode.EXTENDED)
            assertTrue(RomanNumerals.isValid(r, RomanMode.EXTENDED), "invalid: $r")
            val back = RomanNumerals.toArabic(r, RomanMode.EXTENDED)
            assertEquals(n, back, "Failed for $n: $r -> $back")
        }
    }

    // --- Invalid forms should be rejected (STRICT & EXTENDED) ---
    @Test
    fun rejects_invalid_forms() {
        val invalid = listOf(
            "", "IIII", "VV", "LL", "DD",
            "IL", "IC", "IM", "XD", "XM", "VX",
            "IIV", "XXC", "MCMCM", "CMM", "LC", "DM"
        )
        for (s in invalid) {
            assertFalse(RomanNumerals.isValid(s, RomanMode.STRICT), "STRICT accepted `$s`")
            assertFalse(RomanNumerals.isValid(s, RomanMode.EXTENDED), "EXTENDED accepted `$s`")
            assertFails { RomanNumerals.toArabic(s, RomanMode.STRICT) }
            assertFails { RomanNumerals.toArabic(s, RomanMode.EXTENDED) }
        }
    }

    // --- Domain limit errors ---
    @Test
    fun domain_limits_are_enforced() {
        assertFailsWith<IllegalArgumentException> { RomanNumerals.toRoman(0) }
        assertFailsWith<IllegalArgumentException> { RomanNumerals.toRoman(-7) }
        assertFailsWith<IllegalArgumentException> { RomanNumerals.toRoman(4000, RomanMode.STRICT) }
    }
}
