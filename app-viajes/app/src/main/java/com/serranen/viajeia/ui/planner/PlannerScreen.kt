package com.serranen.viajeia.ui.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serranen.viajeia.di.AppContainer
import com.serranen.viajeia.ui.AppViewModelFactory
import com.serranen.viajeia.ui.collectAsStateSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    container: AppContainer,
    onSaved: (Long) -> Unit,
) {
    val vm: PlannerViewModel = viewModel(factory = AppViewModelFactory(container))
    val state by vm.state.collectAsStateSafe()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Planificar con IA") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.destination,
                onValueChange = vm::onDestination,
                label = { Text("Destino (ej. Lisboa)") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.days,
                onValueChange = vm::onDays,
                label = { Text("Días") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.budget,
                onValueChange = vm::onBudget,
                label = { Text("Presupuesto (opcional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.interests,
                onValueChange = vm::onInterests,
                label = { Text("Intereses (gastronomía, museos, naturaleza...)") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = vm::generate,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp,
                    )
                    Text("Generando...")
                } else {
                    Text("Generar itinerario")
                }
            }

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (state.itinerary.isNotBlank()) {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        state.itinerary,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(onClick = vm::generate, modifier = Modifier.weight(1f)) {
                        Text("Regenerar")
                    }
                    Button(
                        onClick = { vm.save(onSaved) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Guardar viaje")
                    }
                }
            }
        }
    }
}
