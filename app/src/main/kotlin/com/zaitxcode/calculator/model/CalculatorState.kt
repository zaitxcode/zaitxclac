package com.zaitxcode.calculator.model

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val previewResult: String = "",
    val isScientificOpen: Boolean = false,
    val history: List<CalculationHistoryItem> = emptyList()
) {
    val displayText: String
        get() = when {
            number1.isEmpty() && operation == null -> "0"
            operation == null -> number1
            number2.isEmpty() -> "$number1 ${operation.symbol}"
            else -> "$number1 ${operation.symbol} $number2"
        }
}
