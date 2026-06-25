package com.serranen.viajeia.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranen.viajeia.data.local.DiaryEntryEntity
import com.serranen.viajeia.data.local.ExpenseEntity
import com.serranen.viajeia.data.local.TripEntity
import com.serranen.viajeia.data.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val repository: TripRepository,
    private val tripId: Long,
) : ViewModel() {

    val trip: StateFlow<TripEntity?> = repository.trip(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val diary: StateFlow<List<DiaryEntryEntity>> = repository.diary(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.expenses(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val total: StateFlow<Double> = repository.expenseTotal(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    fun addDiary(text: String, place: String?) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addDiary(
                DiaryEntryEntity(tripId = tripId, text = text.trim(), placeName = place?.trim())
            )
        }
    }

    fun addExpense(title: String, amount: Double, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    tripId = tripId,
                    title = title.trim(),
                    amount = amount,
                    category = category.ifBlank { "General" },
                )
            )
        }
    }
}
