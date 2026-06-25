package com.serranen.viajeia.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranen.viajeia.data.remote.AiTurn
import com.serranen.viajeia.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(val fromUser: Boolean, val text: String)

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

class ChatViewModel(private val ai: AiRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun send(text: String) {
        val message = text.trim()
        if (message.isBlank() || _state.value.loading) return

        _state.update {
            it.copy(
                messages = it.messages + ChatMessage(fromUser = true, text = message),
                loading = true,
                error = null,
            )
        }

        viewModelScope.launch {
            val history = _state.value.messages.map { AiTurn(it.fromUser, it.text) }
            ai.chat(history)
                .onSuccess { reply ->
                    _state.update {
                        it.copy(
                            messages = it.messages + ChatMessage(fromUser = false, text = reply),
                            loading = false,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(loading = false, error = e.message ?: "Error") }
                }
        }
    }
}
