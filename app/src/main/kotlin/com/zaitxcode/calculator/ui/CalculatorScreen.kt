package com.zaitxcode.calculator.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaitxcode.calculator.model.CalculatorAction
import com.zaitxcode.calculator.model.CalculatorOperation
import com.zaitxcode.calculator.model.CalculatorState
import com.zaitxcode.calculator.ui.components.CalculatorButton
import com.zaitxcode.calculator.ui.history.HistoryBottomSheet
import com.zaitxcode.calculator.ui.theme.OperatorColor

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    onToggleScientific: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHistory by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val buttonSpacing = 10.dp
    val standardRowHeight = 62.dp
    val scientificRowHeight = 42.dp

    // Modern Material 3 Colors
    val actionButtonBg = MaterialTheme.colorScheme.secondaryContainer
    val actionButtonTextColor = MaterialTheme.colorScheme.onSecondaryContainer
    val numberButtonBg = MaterialTheme.colorScheme.surfaceContainerHigh
    val numberButtonTextColor = MaterialTheme.colorScheme.onSurface
    val operatorButtonBg = OperatorColor
    val operatorButtonTextColor = Color.White
    val sciButtonBg = MaterialTheme.colorScheme.surfaceContainer

    fun handleAction(action: CalculatorAction) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onAction(action)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Utilities Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = state.isScientificOpen,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleScientific()
                    },
                    label = { Text("Scientific (Fx)", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Science,
                            contentDescription = "Scientific Mode",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                IconButton(
                    onClick = { showHistory = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 2. Flexible Display Area (Takes all remaining vertical space)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            val textToCopy = if (state.previewResult.isNotEmpty()) {
                                state.previewResult.removePrefix("= ").trim()
                            } else state.displayText
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = state.displayText,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Light,
                    fontSize = if (state.displayText.length > 10) 36.sp else 50.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = if (state.displayText.length > 10) 42.sp else 56.sp
                )

                if (state.previewResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.previewResult,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // 3. Keypad Area (Anchored, Non-overlapping, Proportional)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                // Scientific Keypad Rows (if active)
                AnimatedVisibility(
                    visible = state.isScientificOpen,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        // Sci Row 1: sin, cos, tan, ln, log
                        Row(
                            modifier = Modifier.fillMaxWidth().height(scientificRowHeight),
                            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                        ) {
                            CalculatorButton("sin", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 15) { handleAction(CalculatorAction.Sin) }
                            CalculatorButton("cos", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 15) { handleAction(CalculatorAction.Cos) }
                            CalculatorButton("tan", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 15) { handleAction(CalculatorAction.Tan) }
                            CalculatorButton("ln", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 15) { handleAction(CalculatorAction.Ln) }
                            CalculatorButton("log", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 15) { handleAction(CalculatorAction.Log) }
                        }
                        // Sci Row 2: √, π, e, ^, ±
                        Row(
                            modifier = Modifier.fillMaxWidth().height(scientificRowHeight),
                            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                        ) {
                            CalculatorButton("√", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 18) { handleAction(CalculatorAction.SquareRoot) }
                            CalculatorButton("π", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 18) { handleAction(CalculatorAction.Pi) }
                            CalculatorButton("e", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 18) { handleAction(CalculatorAction.E) }
                            CalculatorButton("^", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 18) { handleAction(CalculatorAction.Operation(CalculatorOperation.Power)) }
                            CalculatorButton("±", Modifier.weight(1f).fillMaxHeight(), sciButtonBg, numberButtonTextColor, fontSize = 18) { handleAction(CalculatorAction.Negate) }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                // Standard Keypad: 5 Rows
                // Row 1: AC, DEL, %, ÷
                Row(
                    modifier = Modifier.fillMaxWidth().height(standardRowHeight),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton("AC", Modifier.weight(1f).fillMaxHeight(), actionButtonBg, actionButtonTextColor, fontSize = 22, fontWeight = FontWeight.SemiBold) { handleAction(CalculatorAction.Clear) }
                    CalculatorButton("⌫", Modifier.weight(1f).fillMaxHeight(), actionButtonBg, actionButtonTextColor, fontSize = 22) { handleAction(CalculatorAction.Delete) }
                    CalculatorButton("%", Modifier.weight(1f).fillMaxHeight(), actionButtonBg, actionButtonTextColor, fontSize = 22) { handleAction(CalculatorAction.Operation(CalculatorOperation.Percent)) }
                    CalculatorButton("÷", Modifier.weight(1f).fillMaxHeight(), operatorButtonBg, operatorButtonTextColor, fontSize = 32) { handleAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }
                }

                // Row 2: 7, 8, 9, ×
                Row(
                    modifier = Modifier.fillMaxWidth().height(standardRowHeight),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton("7", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(7)) }
                    CalculatorButton("8", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(8)) }
                    CalculatorButton("9", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(9)) }
                    CalculatorButton("×", Modifier.weight(1f).fillMaxHeight(), operatorButtonBg, operatorButtonTextColor, fontSize = 32) { handleAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) }
                }

                // Row 3: 4, 5, 6, −
                Row(
                    modifier = Modifier.fillMaxWidth().height(standardRowHeight),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton("4", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(4)) }
                    CalculatorButton("5", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(5)) }
                    CalculatorButton("6", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(6)) }
                    CalculatorButton("−", Modifier.weight(1f).fillMaxHeight(), operatorButtonBg, operatorButtonTextColor, fontSize = 32) { handleAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) }
                }

                // Row 4: 1, 2, 3, +
                Row(
                    modifier = Modifier.fillMaxWidth().height(standardRowHeight),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton("1", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(1)) }
                    CalculatorButton("2", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(2)) }
                    CalculatorButton("3", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(3)) }
                    CalculatorButton("+", Modifier.weight(1f).fillMaxHeight(), operatorButtonBg, operatorButtonTextColor, fontSize = 32) { handleAction(CalculatorAction.Operation(CalculatorOperation.Add)) }
                }

                // Row 5: 0 (span 2), ., =
                Row(
                    modifier = Modifier.fillMaxWidth().height(standardRowHeight),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton("0", Modifier.weight(2f).fillMaxHeight(), numberButtonBg, numberButtonTextColor) { handleAction(CalculatorAction.Number(0)) }
                    CalculatorButton(".", Modifier.weight(1f).fillMaxHeight(), numberButtonBg, numberButtonTextColor, fontSize = 30) { handleAction(CalculatorAction.Decimal) }
                    CalculatorButton("=", Modifier.weight(1f).fillMaxHeight(), operatorButtonBg, operatorButtonTextColor, fontSize = 32) { handleAction(CalculatorAction.Calculate) }
                }
            }
        }

        // History Bottom Sheet
        if (showHistory) {
            HistoryBottomSheet(
                history = state.history,
                onSelectHistory = { handleAction(CalculatorAction.RestoreHistory(it)) },
                onClearHistory = { handleAction(CalculatorAction.ClearHistory) },
                onDismiss = { showHistory = false }
            )
        }
    }
}
