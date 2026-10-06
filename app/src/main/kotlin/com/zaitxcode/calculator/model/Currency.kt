package com.zaitxcode.calculator.model

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String,
    val rateAgainstUsd: Double
)

object CurrencyData {
    val defaultCurrencies = listOf(
        Currency("USD", "US Dollar", "$", "🇺🇸", 1.0),
        Currency("EUR", "Euro", "€", "🇪🇺", 0.92),
        Currency("GBP", "British Pound", "£", "🇬🇧", 0.77),
        Currency("SAR", "Saudi Riyal", "SAR", "🇸🇦", 3.75),
        Currency("EGP", "Egyptian Pound", "EGP", "🇪🇬", 48.60),
        Currency("AED", "UAE Dirham", "AED", "🇦🇪", 3.67),
        Currency("KWD", "Kuwaiti Dinar", "KWD", "🇰🇼", 0.31),
        Currency("QAR", "Qatari Riyal", "QAR", "🇶🇦", 3.64),
        Currency("BHD", "Bahraini Dinar", "BHD", "🇧🇭", 0.38),
        Currency("OMR", "Omani Rial", "OMR", "🇴🇲", 0.38),
        Currency("JOD", "Jordanian Dinar", "JOD", "🇯🇴", 0.71),
        Currency("MAD", "Moroccan Dirham", "MAD", "🇲🇦", 9.85),
        Currency("DZD", "Algerian Dinar", "DZD", "🇩🇿", 133.5),
        Currency("TND", "Tunisian Dinar", "TND", "🇹🇳", 3.09),
        Currency("LYD", "Libyan Dinar", "LYD", "🇱🇾", 4.82),
        Currency("IQD", "Iraqi Dinar", "IQD", "🇮🇶", 1310.0),
        Currency("TRY", "Turkish Lira", "₺", "🇹🇷", 34.25),
        Currency("JPY", "Japanese Yen", "¥", "🇯🇵", 152.40),
        Currency("CNY", "Chinese Yuan", "¥", "🇨🇳", 7.12),
        Currency("CAD", "Canadian Dollar", "CA$", "🇨🇦", 1.39),
        Currency("AUD", "Australian Dollar", "AU$", "🇦🇺", 1.51),
        Currency("CHF", "Swiss Franc", "CHF", "🇨🇭", 0.87),
        Currency("INR", "Indian Rupee", "₹", "🇮🇳", 84.10),
        Currency("RUB", "Russian Ruble", "₽", "🇷🇺", 96.50),
        Currency("BRL", "Brazilian Real", "R$", "🇧🇷", 5.75),
        Currency("KRW", "South Korean Won", "₩", "🇰🇷", 1380.0),
        Currency("SEK", "Swedish Krona", "kr", "🇸🇪", 10.65),
        Currency("SGD", "Singapore Dollar", "S$", "🇸🇬", 1.32),
        Currency("NZD", "New Zealand Dollar", "NZ$", "🇳🇿", 1.66),
        Currency("ZAR", "South African Rand", "R", "🇿🇦", 17.65)
    )
}
