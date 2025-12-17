package com.example.tripplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tripplanner.ui.navigation.TripPlannerNavHost
import com.example.tripplanner.ui.theme.TripPlannerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val isDarkTheme = remember { mutableStateOf(false) }

            TripPlannerTheme(darkTheme = isDarkTheme.value) {
                TripPlannerNavHost(
                    onToggleTheme = {
                        isDarkTheme.value = !isDarkTheme.value
                    },
                    isDarkTheme = isDarkTheme.value
                )
            }
        }
    }
}

