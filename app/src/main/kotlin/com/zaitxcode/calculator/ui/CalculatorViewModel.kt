package com.zaitxcode.calculator.ui

import androidx.lifecycle.ViewModel
import com.zaitxcode.calculator.model.CalculationHistoryItem
import com.zaitxcode.calculator.model.CalculatorAction
import com.zaitxcode.calculator.model.CalculatorOperation
import com.zaitxcode.calculator.model.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun toggleScientific() {
        _state.update { it.copy(isScientificOpen = !it.isScientificOpen) }
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> _state.update {
                it.copy(number1 = "", number2 = "", operation = null, previewResult = "")
            }
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.SquareRoot -> applyUnaryFunction { sqrt(it) }
            is CalculatorAction.Sin -> applyUnaryFunction { sin(Math.toRadians(it)) }
            is CalculatorAction.Cos -> applyUnaryFunction { cos(Math.toRadians(it)) }
            is CalculatorAction.Tan -> applyUnaryFunction { tan(Math.toRadians(it)) }
            is CalculatorAction.Log -> applyUnaryFunction { if (it <= 0) Double.NaN else log10(it) }
            is CalculatorAction.Ln -> applyUnaryFunction { if (it <= 0) Double.NaN else ln(it) }
            is CalculatorAction.Pi -> insertConstant(Math.PI)
            is CalculatorAction.E -> insertConstant(Math.E)
            is CalculatorAction.Negate -> toggleSign()
            is CalculatorAction.RestoreHistory -> restoreHistory(action.item)
            is CalculatorAction.ClearHistory -> _state.update { it.copy(history = emptyList()) }
        }
    }

    private fun enterNumber(number: Int) {
        val current = _state.value
        if (current.operation == null) {
            if (current.number1.length >= MAX_NUM_LENGTH) return
            val newNum1 = if (current.number1 == "0") number.toString() else current.number1 + number
            _state.update { it.copy(number1 = newNum1) }
        } else {
            if (current.number2.length >= MAX_NUM_LENGTH) return
            val newNum2 = if (current.number2 == "0") number.toString() else current.number2 + number
            _state.update {
                it.copy(
                    number2 = newNum2,
                    previewResult = calculatePreview(it.number1, newNum2, it.operation)
                )
            }
        }
    }

    private fun enterDecimal() {
        val current = _state.value
        if (current.operation == null) {
            if (!current.number1.contains(".") && current.number1.length < MAX_NUM_LENGTH) {
                val newNum1 = if (current.number1.isEmpty()) "0." else "${current.number1}."
                _state.update { it.copy(number1 = newNum1) }
            }
        } else {
            if (!current.number2.contains(".") && current.number2.length < MAX_NUM_LENGTH) {
                val newNum2 = if (current.number2.isEmpty()) "0." else "${current.number2}."
                _state.update { it.copy(number2 = newNum2) }
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperation) {
        val current = _state.value
        if (current.number1.isNotEmpty()) {
            if (current.number2.isNotEmpty()) {
                performCalculation()
            }
            _state.update {
                it.copy(
                    operation = operation,
                    previewResult = ""
                )
            }
        } else if (operation == CalculatorOperation.Subtract) {
            _state.update { it.copy(number1 = "-") }
        }
    }

    private fun performDeletion() {
        val current = _state.value
        when {
            current.number2.isNotEmpty() -> {
                val newNum2 = current.number2.dropLast(1)
                _state.update {
                    it.copy(
                        number2 = newNum2,
                        previewResult = if (newNum2.isNotEmpty()) calculatePreview(it.number1, newNum2, it.operation) else ""
                    )
                }
            }
            current.operation != null -> {
                _state.update { it.copy(operation = null, previewResult = "") }
            }
            current.number1.isNotEmpty() -> {
                _state.update { it.copy(number1 = current.number1.dropLast(1)) }
            }
        }
    }

    private fun performCalculation() {
        val current = _state.value
        val num1 = current.number1.toDoubleOrNull()
        val num2 = current.number2.toDoubleOrNull()

        if (num1 != null && num2 != null && current.operation != null) {
            val result = calculate(num1, num2, current.operation)
            val formatted = formatResult(result)
            val expression = "${current.number1} ${current.operation.symbol} ${current.number2}"

            val newHistory = if (formatted != "Error") {
                listOf(CalculationHistoryItem(expression = expression, result = formatted)) + current.history
            } else {
                current.history
            }

            _state.update {
                it.copy(
                    number1 = formatted,
                    number2 = "",
                    operation = null,
                    previewResult = "",
                    history = newHistory.take(50)
                )
            }
        }
    }

    private fun applyUnaryFunction(operation: (Double) -> Double) {
        val current = _state.value
        val target = if (current.operation == null) current.number1 else current.number2
        val value = target.toDoubleOrNull() ?: return
        val result = operation(value)
        val formatted = formatResult(result)

        if (current.operation == null) {
            _state.update { it.copy(number1 = formatted) }
        } else {
            _state.update {
                it.copy(
                    number2 = formatted,
                    previewResult = calculatePreview(it.number1, formatted, it.operation)
                )
            }
        }
    }

    private fun insertConstant(value: Double) {
        val formatted = formatResult(value)
        val current = _state.value
        if (current.operation == null) {
            _state.update { it.copy(number1 = formatted) }
        } else {
            _state.update {
                it.copy(
                    number2 = formatted,
                    previewResult = calculatePreview(it.number1, formatted, it.operation)
                )
            }
        }
    }

    private fun toggleSign() {
        val current = _state.value
        if (current.operation == null) {
            if (current.number1.isNotEmpty() && current.number1 != "0") {
                val newNum1 = if (current.number1.startsWith("-")) current.number1.removePrefix("-") else "-${current.number1}"
                _state.update { it.copy(number1 = newNum1) }
            }
        } else {
            if (current.number2.isNotEmpty() && current.number2 != "0") {
                val newNum2 = if (current.number2.startsWith("-")) current.number2.removePrefix("-") else "-${current.number2}"
                _state.update {
                    it.copy(
                        number2 = newNum2,
                        previewResult = calculatePreview(it.number1, newNum2, it.operation)
                    )
                }
            }
        }
    }

    private fun restoreHistory(item: CalculationHistoryItem) {
        _state.update {
            it.copy(
                number1 = item.result,
                number2 = "",
                operation = null,
                previewResult = ""
            )
        }
    }

    private fun calculatePreview(s1: String, s2: String, op: CalculatorOperation?): String {
        val n1 = s1.toDoubleOrNull() ?: return ""
        val n2 = s2.toDoubleOrNull() ?: return ""
        if (op == null) return ""
        val res = calculate(n1, n2, op)
        return if (res.isNaN() || res.isInfinite()) "Error" else "= " + formatResult(res)
    }

    private fun calculate(n1: Double, n2: Double, op: CalculatorOperation): Double {
        return when (op) {
            is CalculatorOperation.Add -> n1 + n2
            is CalculatorOperation.Subtract -> n1 - n2
            is CalculatorOperation.Multiply -> n1 * n2
            is CalculatorOperation.Divide -> if (n2 == 0.0) Double.NaN else n1 / n2
            is CalculatorOperation.Percent -> n1 * (n2 / 100.0)
            is CalculatorOperation.Power -> n1.pow(n2)
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        return try {
            val bd = BigDecimal(value, MathContext(10, RoundingMode.HALF_UP))
            val stripped = bd.stripTrailingZeros()
            stripped.toPlainString()
        } catch (_: Exception) {
            value.toString().removeSuffix(".0")
        }
    }

    companion object {
        private const val MAX_NUM_LENGTH = 15
    }
}
