# ProGuard rules for Calculator
-keep class com.zaitxcode.calculator.** { *; }

# Keep Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep generated R classes
-keep class com.zaitxcode.calculator.R
-keep class com.zaitxcode.calculator.R$* { *; }
