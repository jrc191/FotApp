package com.example.fotapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val userName: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: "Usuario"
        }

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: false
        }

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun saveDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDark
        }
    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }
}
