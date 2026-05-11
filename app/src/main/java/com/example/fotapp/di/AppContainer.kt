package com.example.fotapp.di

import android.content.Context
import com.example.fotapp.data.local.AppDatabase
import com.example.fotapp.data.network.ApiService
import com.example.fotapp.data.network.MockInterceptor
import com.example.fotapp.data.preferences.UserPreferencesRepository
import com.example.fotapp.data.preferences.dataStore
import com.example.fotapp.data.repository.OfflineFirstPlayerRepository
import com.example.fotapp.data.repository.PlayerRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val playerRepository: PlayerRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(MockInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://mockapi.futconnect.com/") // Base URL ficticia
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val playerRepository: PlayerRepository by lazy {
        OfflineFirstPlayerRepository(
            apiService,
            AppDatabase.getDatabase(context).playerDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
