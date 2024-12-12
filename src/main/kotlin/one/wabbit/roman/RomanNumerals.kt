package one.wabbit.roman

private val romanUnits = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
private val romanSymbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

object RomanNumerals {
    fun toRoman(number: Int): String {
        var number = number
        val roman = StringBuilder()
        for (i in romanUnits.indices) {
            while (number >= romanUnits[i]) {
                roman.append(romanSymbols[i])
                number -= romanUnits[i]
            }
        }
        return roman.toString()
    }

    fun toArabic(roman: String): Int {
        var result = 0
        var prevValue = 0

        // Iterate through the string from right to left
        for (i in roman.length - 1 downTo 0) {

            // Find the value of current Roman numeral
            val currentValue = when (roman[i].toString()) {
                "I" -> 1
                "V" -> 5
                "X" -> 10
                "L" -> 50
                "C" -> 100
                "D" -> 500
                "M" -> 1000
                else -> 0
            }

            // If current value is greater or equal to previous value,
            // add it to result, otherwise subtract it
            if (currentValue >= prevValue) {
                result += currentValue
            } else {
                result -= currentValue
            }

            prevValue = currentValue
        }

        return result
    }
}
