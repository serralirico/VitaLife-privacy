package com.serranen.viajeia.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serranen.viajeia.di.AppContainer
import com.serranen.viajeia.ui.AppViewModelFactory
import com.serranen.viajeia.ui.collectAsStateSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(container: AppContainer) {
    val vm: SettingsViewModel = viewModel(factory = AppViewModelFactory(container))
    val state by vm.state.collectAsStateSafe()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Configuración de la IA", style = MaterialTheme.typography.titleMedium)
            Text(
                "Usa tu propia clave de Gemini (BYOK) o apunta la URL base a tu " +
                    "servidor proxy y deja la clave vacía, como en VitaLife.",
                style = MaterialTheme.typography.bodyMedium,
            )

            OutlinedTextField(
                value = state.config.apiKey,
                onValueChange = vm::onApiKey,
                label = { Text("Clave de API de Gemini (opcional con proxy)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.config.baseUrl,
                onValueChange = vm::onBaseUrl,
                label = { Text("URL base") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.config.model,
                onValueChange = vm::onModel,
                label = { Text("Modelo (ej. gemini-2.0-flash)") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(onClick = vm::save, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar")
            }
            if (state.saved) {
                Text("Guardado ✔", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
