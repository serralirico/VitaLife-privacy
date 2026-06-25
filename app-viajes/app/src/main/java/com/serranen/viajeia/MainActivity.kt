package com.serranen.viajeia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.serranen.viajeia.ui.navigation.ViajeIaApp as ViajeIaUi
import com.serranen.viajeia.ui.theme.ViajeIATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as ViajeIaApp).container
        setContent {
            ViajeIATheme {
                ViajeIaUi(container = container)
            }
        }
    }
}
