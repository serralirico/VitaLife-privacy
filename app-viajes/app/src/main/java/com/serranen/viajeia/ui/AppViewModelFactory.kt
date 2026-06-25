package com.serranen.viajeia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serranen.viajeia.di.AppContainer
import com.serranen.viajeia.ui.chat.ChatViewModel
import com.serranen.viajeia.ui.diary.TripDetailViewModel
import com.serranen.viajeia.ui.home.HomeViewModel
import com.serranen.viajeia.ui.planner.PlannerViewModel
import com.serranen.viajeia.ui.settings.SettingsViewModel

/**
 * Construye los ViewModels a partir del [AppContainer]. [tripId] solo se usa
 * para las pantallas que dependen de un viaje concreto.
 */
class AppViewModelFactory(
    private val container: AppContainer,
    private val tripId: Long = 0,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(HomeViewModel::class.java) ->
            HomeViewModel(container.tripRepository) as T

        modelClass.isAssignableFrom(PlannerViewModel::class.java) ->
            PlannerViewModel(container.aiRepository, container.tripRepository) as T

        modelClass.isAssignableFrom(ChatViewModel::class.java) ->
            ChatViewModel(container.aiRepository) as T

        modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
            SettingsViewModel(container.settingsRepository) as T

        modelClass.isAssignableFrom(TripDetailViewModel::class.java) ->
            TripDetailViewModel(container.tripRepository, tripId) as T

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
