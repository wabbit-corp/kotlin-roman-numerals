package one.wabbit.roman

import java.util.Locale

enum class RomanMode {
    STRICT,
    EXTENDED,
}

object RomanNumerals {
    private val units = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    private val symbols =
        arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

    private val patternExtended = Regex("^M*(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")
    private val patternStrict = Regex("^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")

    private fun patternFor(mode: RomanMode) =
        when (mode) {
            RomanMode.STRICT -> patternStrict
            RomanMode.EXTENDED -> patternExtended
        }

    @JvmOverloads
    fun toRoman(number: Int, mode: RomanMode = RomanMode.EXTENDED): String {
        when (mode) {
            RomanMode.STRICT ->
                require(number in 1..3999) {
                    "Roman numerals (STRICT) support 1..3999; got $number"
                }
            RomanMode.EXTENDED ->
                require(number > 0) { "Roman numerals require positive integers; got $number" }
        }

        var remainder = number
        val out = StringBuilder()
        for (i in units.indices) {
            if (remainder == 0) break
            val u = units[i]
            val cnt = remainder / u
            if (cnt != 0) {
                repeat(cnt) { out.append(symbols[i]) }
                remainder -= cnt * u
            }
        }
        return out.toString()
    }

    private fun normalizeInput(rawRoman: String): String = rawRoman.trim().uppercase(Locale.ROOT)

    /** Parse Roman numerals to Arabic integers with validation. */
    @JvmOverloads
    fun toArabic(rawRoman: String, mode: RomanMode = RomanMode.EXTENDED): Int {
        val roman = normalizeInput(rawRoman)
        if (roman.isEmpty()) throw IllegalArgumentException("Empty Roman numeral")
        val pat = patternFor(mode)
        require(pat.matches(roman)) { "Invalid Roman numeral ($mode): $rawRoman" }

        var total = 0
        var prev = 0
        // Walk right-to-left
        for (ch in roman.reversed()) {
            val v =
                when (ch) {
                    'I' -> 1
                    'V' -> 5
                    'X' -> 10
                    'L' -> 50
                    'C' -> 100
                    'D' -> 500
                    'M' -> 1000
                    else ->
                        throw IllegalArgumentException("Invalid character in Roman numeral: '$ch'")
                }
            total += if (v >= prev) v else -v
            prev = v
        }
        return total
    }

    /** Non-throwing parse. */
    fun tryToArabic(rawRoman: String, mode: RomanMode = RomanMode.EXTENDED): Result<Int> =
        runCatching {
            toArabic(rawRoman, mode)
        }

    /** Validate without parsing. */
    @JvmOverloads
    fun isValid(rawRoman: String, mode: RomanMode = RomanMode.EXTENDED): Boolean {
        val roman = normalizeInput(rawRoman)
        if (roman.isEmpty()) return false
        return patternFor(mode).matches(roman)
    }
}
