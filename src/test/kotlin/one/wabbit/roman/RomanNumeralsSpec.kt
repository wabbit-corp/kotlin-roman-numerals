package one.wabbit.roman

import kotlin.test.Test
import kotlin.test.assertEquals

class RomanNumeralsSpec {
    @Test
    fun test() {
        for (i in 1..10000) {
            val roman = RomanNumerals.toRoman(i)
            val back = RomanNumerals.toArabic(roman)
            assertEquals(i, back, "Failed for $i: $roman -> $back")
        }
    }
}
