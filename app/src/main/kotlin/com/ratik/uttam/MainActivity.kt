package com.ratik.uttam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.ratik.uttam.ui.MainApp

class MainActivity {
    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            WindowCompat.setDecorFitsSystemWindows(window, true)
            setContent {
                MainApp()
            }
        }
    }
}