package com.serranen.viajeia.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranen.viajeia.data.local.TripEntity
import com.serranen.viajeia.data.repository.AiRepository
import com.serranen.viajeia.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlannerState(
    val destination: String = "",
    val days: String = "3",
    val budget: String = "",
    val interests: String = "",
    val loading: Boolean = false,
    val itinerary: String = "",
    val error: String? = null,
)

class PlannerViewModel(
    private val ai: AiRepository,
    private val trips: TripRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PlannerState())
    val state: StateFlow<PlannerState> = _state.asStateFlow()

    fun onDestination(v: String) = _state.update { it.copy(destination = v) }
    fun onDays(v: String) = _state.update { it.copy(days = v.filter(Char::isDigit)) }
    fun onBudget(v: String) = _state.update { it.copy(budget = v) }
    fun onInterests(v: String) = _state.update { it.copy(interests = v) }

    fun generate() {
        val s = _state.value
        if (s.destination.isBlank()) {
            _state.update { it.copy(error = "Indica un destino") }
            return
        }
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val result = ai.planItinerary(
                destination = s.destination,
                days = s.days.toIntOrNull() ?: 3,
                budget = s.budget,
                interests = s.interests,
            )
            result.onSuccess { text ->
                _state.update { it.copy(loading = false, itinerary = text) }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message ?: "Error al generar") }
            }
        }
    }

    /** Guarda el viaje con su itinerario y devuelve el id por [onSaved]. */
    fun save(onSaved: (Long) -> Unit) {
        val s = _state.value
        if (s.itinerary.isBlank()) return
        viewModelScope.launch {
            val id = trips.saveTrip(
                TripEntity(
                    title = s.destination,
                    destination = s.destination,
                    itinerary = s.itinerary,
                )
            )
            onSaved(id)
        }
    }
}
