package com.example.ozmade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.ozmade.auth.AuthNavHost
import com.example.ozmade.ui.theme.OzMadeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OzMadeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AuthNavHost(onAuthSuccess = { /* TODO: Navigate to main app screen */ })
                }
            }
        }
    }
}
