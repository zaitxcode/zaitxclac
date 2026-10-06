package com.zaitxcode.calculator.model

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    // Scientific
    object SquareRoot : CalculatorAction()
    object Sin : CalculatorAction()
    object Cos : CalculatorAction()
    object Tan : CalculatorAction()
    object Log : CalculatorAction()
    object Ln : CalculatorAction()
    object Pi : CalculatorAction()
    object E : CalculatorAction()
    object Negate : CalculatorAction()
    data class RestoreHistory(val item: CalculationHistoryItem) : CalculatorAction()
    object ClearHistory : CalculatorAction()
}
