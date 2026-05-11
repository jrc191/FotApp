package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val preferencesRepository: UserPreferencesRepository) : ViewModel() {

    val userName: StateFlow<String> = preferencesRepository.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Usuario"
        )

    val isDarkMode: StateFlow<Boolean> = preferencesRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun saveUserName(name: String) {
        viewModelScope.launch {
            preferencesRepository.saveUserName(name)
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveDarkMode(isDark)
        }
    }
}
