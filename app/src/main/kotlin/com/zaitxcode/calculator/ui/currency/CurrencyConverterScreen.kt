package com.zaitxcode.calculator.ui.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaitxcode.calculator.model.Currency
import com.zaitxcode.calculator.ui.components.CalculatorButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    state: CurrencyUiState,
    onEnterDigit: (Int) -> Unit,
    onEnterDecimal: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onSwap: () -> Unit,
    onRefreshRates: () -> Unit,
    onSelectFromCurrency: (Currency) -> Unit,
    onSelectToCurrency: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    var pickingCurrencyFor by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Live Status & Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (state.isOnline) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isOnline) {
                                if (state.lastUpdated.isNotEmpty()) "Live Rates • ${state.lastUpdated}" else "Live Rates (Connected)"
                            } else "Offline Mode (Cached)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(
                            onClick = onRefreshRates,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh live rates",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Rate Exchange Banner
                val unitRate = if (state.fromCurrency.rateAgainstUsd != 0.0) {
                    state.toCurrency.rateAgainstUsd / state.fromCurrency.rateAgainstUsd
                } else 0.0
                Text(
                    text = "1 ${state.fromCurrency.code} = ${String.format("%.4f", unitRate)} ${state.toCurrency.code}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 2.dp)
                )

                // From Card
                CurrencyCard(
                    title = "From",
                    currency = state.fromCurrency,
                    amount = state.inputAmount,
                    isEditable = true,
                    onClickCurrency = {
                        searchQuery = ""
                        pickingCurrencyFor = "from"
                    }
                )

                // Swap Button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onSwap,
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap currencies",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // To Card
                CurrencyCard(
                    title = "To",
                    currency = state.toCurrency,
                    amount = state.convertedAmount,
                    isEditable = false,
                    onClickCurrency = {
                        searchQuery = ""
                        pickingCurrencyFor = "to"
                    }
                )

                // Quick presets
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(1, 10, 50, 100, 500, 1000)
                    items(presets) { amount ->
                        FilterChip(
                            selected = state.inputAmount == amount.toString(),
                            onClick = {
                                onClear()
                                amount.toString().forEach { ch -> onEnterDigit(ch.digitToInt()) }
                            },
                            label = { Text("$amount", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    }
                }
            }

            // Dedicated Numeric Keypad (LTR)
            val keypadSpacing = 10.dp
            val numBg = MaterialTheme.colorScheme.surfaceContainerHigh
            val numText = MaterialTheme.colorScheme.onSurface
            val actionBg = MaterialTheme.colorScheme.secondaryContainer
            val actionText = MaterialTheme.colorScheme.onSecondaryContainer

            val rowHeight = 56.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(keypadSpacing)
            ) {
                // Row 1: 7, 8, 9, C
                Row(
                    modifier = Modifier.fillMaxWidth().height(rowHeight),
                    horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                ) {
                    CalculatorButton("7", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(7) }
                    CalculatorButton("8", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(8) }
                    CalculatorButton("9", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(9) }
                    CalculatorButton("C", Modifier.weight(1f).fillMaxHeight(), actionBg, actionText, fontWeight = FontWeight.SemiBold) { onClear() }
                }

                // Row 2: 4, 5, 6, ⌫
                Row(
                    modifier = Modifier.fillMaxWidth().height(rowHeight),
                    horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                ) {
                    CalculatorButton("4", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(4) }
                    CalculatorButton("5", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(5) }
                    CalculatorButton("6", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(6) }
                    CalculatorButton("⌫", Modifier.weight(1f).fillMaxHeight(), actionBg, actionText) { onDelete() }
                }

                // Row 3: 1, 2, 3, 00
                Row(
                    modifier = Modifier.fillMaxWidth().height(rowHeight),
                    horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                ) {
                    CalculatorButton("1", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(1) }
                    CalculatorButton("2", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(2) }
                    CalculatorButton("3", Modifier.weight(1f).fillMaxHeight(), numBg, numText) { onEnterDigit(3) }
                    CalculatorButton("00", Modifier.weight(1f).fillMaxHeight(), numBg, numText, fontSize = 22) {
                        onEnterDigit(0)
                        onEnterDigit(0)
                    }
                }

                // Row 4: 0 (wide), ., =
                Row(
                    modifier = Modifier.fillMaxWidth().height(rowHeight),
                    horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                ) {
                    CalculatorButton("0", Modifier.weight(2f).fillMaxHeight(), numBg, numText) { onEnterDigit(0) }
                    CalculatorButton(".", Modifier.weight(1f).fillMaxHeight(), numBg, numText, fontSize = 28) { onEnterDecimal() }
                    CalculatorButton("=", Modifier.weight(1f).fillMaxHeight(), actionBg, actionText) { /* Keep current */ }
                }
            }
        }

        // Currency Picker BottomSheet with Search
        if (pickingCurrencyFor != null) {
            val filteredCurrencies = remember(searchQuery, state.currencies) {
                if (searchQuery.isBlank()) {
                    state.currencies
                } else {
                    val query = searchQuery.trim().lowercase()
                    state.currencies.filter {
                        it.code.lowercase().contains(query) || it.name.lowercase().contains(query)
                    }
                }
            }

            ModalBottomSheet(
                onDismissRequest = { pickingCurrencyFor = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (pickingCurrencyFor == "from") "Convert From" else "Convert To",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search by code or country...", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredCurrencies, key = { it.code }) { currency ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (pickingCurrencyFor == "from") {
                                            onSelectFromCurrency(currency)
                                        } else {
                                            onSelectToCurrency(currency)
                                        }
                                        pickingCurrencyFor = null
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = currency.flag, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${currency.code} (${currency.symbol})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currency.name,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = String.format("%.2f", currency.rateAgainstUsd),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyCard(
    title: String,
    currency: Currency,
    amount: String,
    isEditable: Boolean,
    onClickCurrency: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Currency Selector Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClickCurrency() }
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = currency.flag, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = currency.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currency.name,
                            fontSize = 10.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Amount Display
                Text(
                    text = amount,
                    fontSize = if (amount.length > 8) 24.sp else 30.sp,
                    fontWeight = if (isEditable) FontWeight.Medium else FontWeight.Bold,
                    color = if (isEditable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                )
            }
        }
    }
}
