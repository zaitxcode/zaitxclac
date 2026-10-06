package com.zaitxcode.calculator.ui.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaitxcode.calculator.model.Currency
import com.zaitxcode.calculator.model.CurrencyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL

data class CurrencyUiState(
    val fromCurrency: Currency = CurrencyData.defaultCurrencies[0], // USD
    val toCurrency: Currency = CurrencyData.defaultCurrencies[4],   // EGP
    val inputAmount: String = "1",
    val convertedAmount: String = "48.60",
    val currencies: List<Currency> = CurrencyData.defaultCurrencies,
    val isLoading: Boolean = false,
    val isOnline: Boolean = false,
    val lastUpdated: String = ""
)

class CurrencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        updateConversion(_uiState.value.inputAmount)
        fetchRates()
    }

    fun fetchRates() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val url = URL("https://open.er-api.com/v6/latest/USD")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                }

                if (connection.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val json = JSONObject(response)
                    if (json.optString("result") == "success") {
                        val rates = json.getJSONObject("rates")
                        val timeLastUpdate = json.optString("time_last_update_utc", "")
                        val formattedTime = if (timeLastUpdate.length >= 16) {
                            timeLastUpdate.substring(0, 16)
                        } else timeLastUpdate

                        val updatedList = _uiState.value.currencies.map { cur ->
                            if (rates.has(cur.code)) {
                                cur.copy(rateAgainstUsd = rates.getDouble(cur.code))
                            } else cur
                        }

                        val currentFrom = _uiState.value.fromCurrency
                        val currentTo = _uiState.value.toCurrency

                        val newFrom = updatedList.find { it.code == currentFrom.code } ?: currentFrom
                        val newTo = updatedList.find { it.code == currentTo.code } ?: currentTo

                        _uiState.update {
                            it.copy(
                                currencies = updatedList,
                                fromCurrency = newFrom,
                                toCurrency = newTo,
                                isLoading = false,
                                isOnline = true,
                                lastUpdated = formattedTime
                            )
                        }
                        updateConversion(_uiState.value.inputAmount)
                        return@launch
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _uiState.update { it.copy(isLoading = false, isOnline = false) }
        }
    }

    fun setFromCurrency(currency: Currency) {
        _uiState.update { it.copy(fromCurrency = currency) }
        updateConversion(_uiState.value.inputAmount)
    }

    fun setToCurrency(currency: Currency) {
        _uiState.update { it.copy(toCurrency = currency) }
        updateConversion(_uiState.value.inputAmount)
    }

    fun swapCurrencies() {
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency
            )
        }
        updateConversion(_uiState.value.inputAmount)
    }

    fun enterDigit(digit: Int) {
        val current = _uiState.value.inputAmount
        if (current.length >= 12) return
        val newAmount = if (current == "0") digit.toString() else current + digit
        _uiState.update { it.copy(inputAmount = newAmount) }
        updateConversion(newAmount)
    }

    fun enterDecimal() {
        val current = _uiState.value.inputAmount
        if (!current.contains(".") && current.length < 12) {
            val newAmount = if (current.isEmpty()) "0." else "$current."
            _uiState.update { it.copy(inputAmount = newAmount) }
            updateConversion(newAmount)
        }
    }

    fun deleteDigit() {
        val current = _uiState.value.inputAmount
        val newAmount = if (current.isNotEmpty()) current.dropLast(1) else ""
        val finalAmount = if (newAmount.isEmpty()) "0" else newAmount
        _uiState.update { it.copy(inputAmount = finalAmount) }
        updateConversion(finalAmount)
    }

    fun clear() {
        _uiState.update { it.copy(inputAmount = "0") }
        updateConversion("0")
    }

    private fun updateConversion(amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val state = _uiState.value
        val result = if (state.fromCurrency.rateAgainstUsd != 0.0) {
            (amount / state.fromCurrency.rateAgainstUsd) * state.toCurrency.rateAgainstUsd
        } else {
            0.0
        }

        val formatted = try {
            val bd = BigDecimal(result, MathContext(8, RoundingMode.HALF_UP))
            bd.setScale(2, RoundingMode.HALF_UP).toPlainString()
        } catch (_: Exception) {
            String.format("%.2f", result)
        }

        _uiState.update { it.copy(convertedAmount = formatted) }
    }
}
