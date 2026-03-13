package com.example.login.ui.Chat

import androidx.lifecycle.ViewModel
import com.example.login.model.MensajesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow(ChatUIState())
    val uiState: StateFlow<ChatUIState> = _uiState.asStateFlow()

    init {
        cargarChat()
    }

    private fun cargarChat() {
        _uiState.update {
            it.copy(
                mensajes    = MensajesData.mensajesQueen,
                nombreGrupo = "Queen",
                isLoading   = false
            )
        }
    }

    fun onMensajeChange(texto: String) {
        _uiState.update { it.copy(mensajeTexto = texto) }
    }

    // Limpia el campo luego de enviar
    // (en una app real aquí se llamaría al repositorio para persistir)
    fun enviarMensaje() {
        val texto = _uiState.value.mensajeTexto.trim()
        if (texto.isBlank()) return
        _uiState.update { it.copy(mensajeTexto = "") }
    }
}