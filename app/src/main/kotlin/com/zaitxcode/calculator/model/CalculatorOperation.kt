package com.zaitxcode.calculator.model

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("−")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("÷")
    object Percent : CalculatorOperation("%")
    object Power : CalculatorOperation("^")
}
