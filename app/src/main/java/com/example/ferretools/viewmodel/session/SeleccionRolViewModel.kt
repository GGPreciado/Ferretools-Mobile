package com.example.ferretools.viewmodel.session

import androidx.lifecycle.ViewModel
import com.example.ferretools.model.enums.RolUsuario
import com.example.ferretools.model.states.registro.RolUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SeleccionRolViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(RolUiState())
    val uiState = _uiState.asStateFlow()

    fun updateRol(rol: RolUsuario) {
        _uiState.update {
            it.copy(rolUsuario = rol)
        }
    }
}