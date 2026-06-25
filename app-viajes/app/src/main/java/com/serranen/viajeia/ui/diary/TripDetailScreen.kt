package com.serranen.viajeia.ui.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serranen.viajeia.di.AppContainer
import com.serranen.viajeia.ui.AppViewModelFactory
import com.serranen.viajeia.ui.collectAsStateSafe

private val tabs = listOf("Itinerario", "Diario", "Gastos")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    container: AppContainer,
    tripId: Long,
    onBack: () -> Unit,
) {
    val vm: TripDetailViewModel = viewModel(
        factory = AppViewModelFactory(container, tripId)
    )
    val trip by vm.trip.collectAsStateSafe()
    val diary by vm.diary.collectAsStateSafe()
    val expenses by vm.expenses.collectAsStateSafe()
    val total by vm.total.collectAsStateSafe()

    var tab by remember { mutableIntStateOf(0) }
    var showDiaryDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.title ?: "Viaje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
            )
        },
        floatingActionButton = {
            when (tab) {
                1 -> FloatingActionButton(onClick = { showDiaryDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir entrada")
                }
                2 -> FloatingActionButton(onClick = { showExpenseDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir gasto")
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
                }
            }
            when (tab) {
                0 -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Text(
                        trip?.itinerary?.ifBlank { "Sin itinerario." } ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                1 -> LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(diary, key = { it.id }) { entry ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                entry.placeName?.let {
                                    Text(it, style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                                Text(entry.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                2 -> Column(Modifier.fillMaxSize()) {
                    Text(
                        "Total: %.2f €".format(total),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp, end = 16.dp, bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(expenses, key = { it.id }) { e ->
                            Card(Modifier.fillMaxWidth()) {
                                Row(
                                    Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(e.title, style = MaterialTheme.typography.bodyLarge)
                                        Text(e.category, style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                    Text("%.2f %s".format(e.amount, e.currency),
                                        style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDiaryDialog) {
        DiaryDialog(
            onDismiss = { showDiaryDialog = false },
            onConfirm = { text, place ->
                vm.addDiary(text, place)
                showDiaryDialog = false
            },
        )
    }
    if (showExpenseDialog) {
        ExpenseDialog(
            onDismiss = { showExpenseDialog = false },
            onConfirm = { title, amount, category ->
                vm.addExpense(title, amount, category)
                showExpenseDialog = false
            },
        )
    }
}

@Composable
private fun DiaryDialog(onDismiss: () -> Unit, onConfirm: (String, String?) -> Unit) {
    var text by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva entrada de diario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(place, { place = it }, label = { Text("Lugar (opcional)") })
                OutlinedTextField(text, { text = it }, label = { Text("¿Qué ha pasado?") })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(text, place) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun ExpenseDialog(onDismiss: () -> Unit, onConfirm: (String, Double, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo gasto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Concepto") })
                OutlinedTextField(
                    amount,
                    { amount = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Importe") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(category, { category = it }, label = { Text("Categoría") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val value = amount.replace(',', '.').toDoubleOrNull() ?: 0.0
                onConfirm(title, value, category)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
