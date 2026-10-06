package com.zaitxcode.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zaitxcode.calculator.ui.MainAppScreen
import com.zaitxcode.calculator.ui.theme.ZaitXCalcTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZaitXCalcTheme {
                MainAppScreen()
            }
        }
    }
}
