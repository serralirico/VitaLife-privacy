package com.serranen.viajeia.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranen.viajeia.data.settings.AiConfig
import com.serranen.viajeia.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val config: AiConfig = AiConfig(),
    val saved: Boolean = false,
)

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.aiConfig.collect { config ->
                _state.update { it.copy(config = config) }
            }
        }
    }

    fun onApiKey(v: String) = _state.update { it.copy(config = it.config.copy(apiKey = v), saved = false) }
    fun onBaseUrl(v: String) = _state.update { it.copy(config = it.config.copy(baseUrl = v), saved = false) }
    fun onModel(v: String) = _state.update { it.copy(config = it.config.copy(model = v), saved = false) }

    fun save() {
        viewModelScope.launch {
            repository.save(_state.value.config)
            _state.update { it.copy(saved = true) }
        }
    }
}
