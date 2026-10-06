package com.zaitxcode.calculator.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaitxcode.calculator.ui.currency.CurrencyConverterScreen
import com.zaitxcode.calculator.ui.currency.CurrencyViewModel

@Composable
fun MainAppScreen(
    calculatorViewModel: CalculatorViewModel = viewModel(),
    currencyViewModel: CurrencyViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val calcState by calculatorViewModel.state.collectAsState()
    val currencyState by currencyViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Main Screen Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    CalculatorScreen(
                        state = calcState,
                        onAction = calculatorViewModel::onAction,
                        onToggleScientific = calculatorViewModel::toggleScientific
                    )
                }
                1 -> {
                    CurrencyConverterScreen(
                        state = currencyState,
                        onEnterDigit = currencyViewModel::enterDigit,
                        onEnterDecimal = currencyViewModel::enterDecimal,
                        onDelete = currencyViewModel::deleteDigit,
                        onClear = currencyViewModel::clear,
                        onSwap = currencyViewModel::swapCurrencies,
                        onRefreshRates = currencyViewModel::fetchRates,
                        onSelectFromCurrency = currencyViewModel::setFromCurrency,
                        onSelectToCurrency = currencyViewModel::setToCurrency
                    )
                }
            }
        }

        // Liquid Glass Floating Dock (Icons Only, Translucent & Fluid)
        LiquidGlassFloatingDock(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun LiquidGlassFloatingDock(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val haptic = LocalHapticFeedback.current

    // Liquid Glass Capsule Styling
    val capsuleBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0x3DFFFFFF), // Top specular liquid reflection
                Color(0x1AFFFFFF),
                Color(0x1F222830)  // Deep translucent glass bottom
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0x99FFFFFF), // Bright crystal reflection
                Color(0x66FFFFFF),
                Color(0xCCF0F2F6)  // Frosted white glass
            )
        )
    }

    val glassBorderStroke = BorderStroke(
        width = 1.2.dp,
        brush = Brush.verticalGradient(
            listOf(
                Color(0x8CFFFFFF), // Crisp top light rim
                Color(0x26FFFFFF)  // Subtle lower rim
            )
        )
    )

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 14.dp)
            .wrapContentWidth()
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x33000000),
                spotColor = if (isDark) Color(0x99000000) else Color(0x22000000)
            )
            .clip(CircleShape)
            .background(capsuleBrush)
            .border(glassBorderStroke, CircleShape)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Calculator Icon Button
            LiquidGlassIconButton(
                selected = selectedTab == 0,
                icon = Icons.Filled.Calculate,
                unselectedIcon = Icons.Outlined.Calculate,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onTabSelected(0)
                }
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Currency Icon Button
            LiquidGlassIconButton(
                selected = selectedTab == 1,
                icon = Icons.Filled.CurrencyExchange,
                unselectedIcon = Icons.Outlined.CurrencyExchange,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onTabSelected(1)
                }
            )
        }
    }
}

@Composable
private fun LiquidGlassIconButton(
    selected: Boolean,
    icon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "liquid_scale"
    )

    // Liquid droplet background for active tab
    val activeDropletBrush = Brush.verticalGradient(
        listOf(
            Color(0xEEFF9500), // Vibrant amber liquid gleam
            Color(0xCCEA580C)
        )
    )

    val inactiveDropletBg = if (isDark) Color(0x1FFFFFFF) else Color(0x1F000000)
    val dropletBorder = if (selected) {
        BorderStroke(1.dp, Color(0x99FFFFFF))
    } else {
        BorderStroke(0.8.dp, if (isDark) Color(0x2EFFFFFF) else Color(0x1A000000))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(52.dp)
            .scale(scale)
            .clip(CircleShape)
            .then(
                if (selected) {
                    Modifier
                        .background(activeDropletBrush)
                        .border(dropletBorder, CircleShape)
                } else {
                    Modifier
                        .background(inactiveDropletBg)
                        .border(dropletBorder, CircleShape)
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = if (selected) icon else unselectedIcon,
            contentDescription = null,
            tint = if (selected) Color.White else (if (isDark) Color(0xCCFFFFFF) else Color(0x991E2124)),
            modifier = Modifier.size(26.dp)
        )
    }
}
