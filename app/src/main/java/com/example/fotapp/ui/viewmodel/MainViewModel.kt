package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val themeMode: StateFlow<Int> = preferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferencesRepository.THEME_SYSTEM
        )

    init {
        checkAndRenewSession()
    }

    /**
     * Al arrancar la app:
     * - Si la sesión está activa → renueva el timestamp (otros 30 días).
     * - Si la sesión caducó    → cierra sesión automáticamente.
     */
    private fun checkAndRenewSession() {
        viewModelScope.launch {
            if (preferencesRepository.isSessionValid()) {
                preferencesRepository.updateLastActiveTime()
            } else {
                // Sesión caducada: forzamos logout silencioso
                preferencesRepository.logout()
            }
        }
    }
}
