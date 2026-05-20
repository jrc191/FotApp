package com.example.fotapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    // ── Flujos observables ────────────────────────────────────────────────

    val userName: Flow<String> = dataStore.data
        .map { it[USER_NAME_KEY] ?: "Usuario" }

    val themeMode: Flow<Int> = dataStore.data
        .map { it[THEME_MODE_KEY] ?: THEME_SYSTEM }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { it[IS_LOGGED_IN_KEY] ?: false }

    val memberSinceDate: Flow<String> = dataStore.data
        .map { it[MEMBER_SINCE_KEY] ?: "2024" }

    // ── Sesión ────────────────────────────────────────────────────────────

    /**
     * Comprueba si la sesión sigue activa.
     * Caduca tras [SESSION_EXPIRY_DAYS] días sin abrir la app.
     */
    suspend fun isSessionValid(): Boolean {
        val prefs = dataStore.data.first()
        val loggedIn = prefs[IS_LOGGED_IN_KEY] ?: false
        if (!loggedIn) return false

        val lastActive = prefs[LAST_ACTIVE_KEY] ?: return false
        val elapsed = System.currentTimeMillis() - lastActive
        return elapsed < SESSION_EXPIRY_DAYS * 24 * 60 * 60 * 1000L
    }

    /** Llámalo cada vez que el usuario abre la app para renovar la sesión. */
    suspend fun updateLastActiveTime() {
        dataStore.edit { it[LAST_ACTIVE_KEY] = System.currentTimeMillis() }
    }

    // ── Login / Logout ────────────────────────────────────────────────────

    suspend fun login(name: String) {
        dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
            prefs[IS_LOGGED_IN_KEY] = true
            prefs[LAST_ACTIVE_KEY] = System.currentTimeMillis()
            if (prefs[MEMBER_SINCE_KEY] == null) {
                prefs[MEMBER_SINCE_KEY] =
                    java.util.Calendar.getInstance().get(java.util.Calendar.YEAR).toString()
            }
        }
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = false
            prefs.remove(LAST_ACTIVE_KEY)
        }
    }

    // ── Preferencias generales ────────────────────────────────────────────

    suspend fun saveUserName(name: String) {
        dataStore.edit { it[USER_NAME_KEY] = name }
    }

    suspend fun saveThemeMode(mode: Int) {
        dataStore.edit { it[THEME_MODE_KEY] = mode }
    }

    // ── Keys y constantes ─────────────────────────────────────────────────

    companion object {
        val USER_NAME_KEY      = stringPreferencesKey("user_name")
        val THEME_MODE_KEY     = androidx.datastore.preferences.core.intPreferencesKey("theme_mode")
        val IS_LOGGED_IN_KEY   = booleanPreferencesKey("is_logged_in")
        val MEMBER_SINCE_KEY   = stringPreferencesKey("member_since")
        val LAST_ACTIVE_KEY    = longPreferencesKey("last_active_time")

        const val THEME_SYSTEM = 0
        const val THEME_LIGHT  = 1
        const val THEME_DARK   = 2

        /** Días que la sesión permanece activa sin abrir la app */
        const val SESSION_EXPIRY_DAYS = 30L
    }
}
