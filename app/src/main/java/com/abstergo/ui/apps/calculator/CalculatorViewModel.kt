package com.abstergo.ui.apps.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {

    private val _display = MutableStateFlow("0")
    val display: StateFlow<String> = _display.asStateFlow()

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private var currentNumber = ""
    private var operator = ""
    private var firstOperand: Double? = null
    private var shouldResetDisplay = false

    fun onDigitPressed(digit: String) {
        if (shouldResetDisplay) {
            currentNumber = ""
            shouldResetDisplay = false
        }
        if (currentNumber == "0" && digit != ".") {
            currentNumber = digit
        } else {
            currentNumber += digit
        }
        _display.value = currentNumber
    }

    fun onOperatorPressed(op: String) {
        if (currentNumber.isNotEmpty() && firstOperand != null && operator.isNotEmpty()) {
            calculateResult()
        }
        firstOperand = currentNumber.toDoubleOrNull()
        operator = op
        _expression.value = "${firstOperand} $op"
        shouldResetDisplay = true
    }

    fun onEqualsPressed() {
        if (firstOperand != null && operator.isNotEmpty() && currentNumber.isNotEmpty()) {
            calculateResult()
            _expression.value = ""
            firstOperand = null
            operator = ""
        }
    }

    fun onClearPressed() {
        currentNumber = ""
        firstOperand = null
        operator = ""
        _display.value = "0"
        _expression.value = ""
        shouldResetDisplay = false
    }

    fun onBackspacePressed() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
            _display.value = currentNumber.ifEmpty { "0" }
        }
    }

    fun onToggleSign() {
        if (currentNumber.isNotEmpty() && currentNumber != "0") {
            currentNumber = if (currentNumber.startsWith("-")) {
                currentNumber.drop(1)
            } else {
                "-$currentNumber"
            }
            _display.value = currentNumber
        }
    }

    fun onPercentPressed() {
        val num = currentNumber.toDoubleOrNull() ?: return
        currentNumber = (num / 100).toString()
        _display.value = currentNumber
    }

    private fun calculateResult() {
        val secondOperand = currentNumber.toDoubleOrNull() ?: return
        val first = firstOperand ?: return

        val result = when (operator) {
            "+" -> first + secondOperand
            "-" -> first - secondOperand
            "×" -> first * secondOperand
            "÷" -> if (secondOperand != 0.0) first / secondOperand else Double.NaN
            else -> return
        }

        val resultStr = if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }

        currentNumber = resultStr
        _display.value = resultStr
        shouldResetDisplay = true
    }
}
