package com.serranen.viajeia.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranen.viajeia.data.local.TripEntity
import com.serranen.viajeia.data.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: TripRepository) : ViewModel() {
    val trips: StateFlow<List<TripEntity>> = repository.trips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
