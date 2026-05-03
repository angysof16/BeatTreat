package com.example.beattreat


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.beattreat.ui.theme.BeatTreatTheme
import dagger.hilt.android.AndroidEntryPoint

// MainActivity - Punto de entrada de la aplicación

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeatTreatTheme {
                BeatTreatApp()
            }
        }
    }
}