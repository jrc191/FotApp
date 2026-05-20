package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fotapp.FutConnectApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            PlayerListViewModel(
                futbolApplication().container.playerRepository
            )
        }
        initializer {
            FavoriteViewModel(
                futbolApplication().container.playerRepository
            )
        }
        initializer {
            ProfileViewModel(
                futbolApplication().container.userPreferencesRepository,
                futbolApplication().container.playerRepository
            )
        }
        initializer {
            PlayerDetailViewModel(
                futbolApplication().container.playerRepository,
                futbolApplication().container.userPreferencesRepository
            )
        }
        initializer {
            MainViewModel(
                futbolApplication().container.userPreferencesRepository
            )
        }
    }
}

fun CreationExtras.futbolApplication(): FutConnectApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FutConnectApplication)
