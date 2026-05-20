package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.preferences.UserPreferencesRepository
import com.example.fotapp.data.repository.PlayerRepository
import com.example.fotapp.model.Comment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val userName: StateFlow<String> = preferencesRepository.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Usuario"
        )

    val themeMode: StateFlow<Int> = preferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferencesRepository.THEME_SYSTEM
        )

    val isLoggedIn: StateFlow<Boolean> = preferencesRepository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val favoritesCount: StateFlow<Int> = playerRepository.getFavoriteCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val commentsCount: StateFlow<Int> = playerRepository.getTotalCommentsCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allComments: StateFlow<List<Comment>> = playerRepository.getAllComments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val memberSince: StateFlow<String> = preferencesRepository.memberSinceDate
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "2024"
        )

    fun login(name: String) {
        viewModelScope.launch {
            preferencesRepository.login(name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.logout()
        }
    }

    fun saveUserName(name: String) {
        viewModelScope.launch {
            preferencesRepository.saveUserName(name)
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            preferencesRepository.saveThemeMode(mode)
        }
    }
}
