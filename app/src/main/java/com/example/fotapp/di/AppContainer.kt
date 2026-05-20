package com.example.fotapp.di

import android.content.Context
import com.example.fotapp.BuildConfig
import com.example.fotapp.data.local.AppDatabase
import com.example.fotapp.data.network.ApiFootballService
import com.example.fotapp.data.network.ApiService
import com.example.fotapp.data.network.RapidApiFootballService
import com.example.fotapp.data.preferences.UserPreferencesRepository
import com.example.fotapp.data.preferences.dataStore
import com.example.fotapp.data.repository.OfflineFirstPlayerRepository
import com.example.fotapp.data.repository.PlayerRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val playerRepository: PlayerRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    // ── Interceptors compartidos ────────────────────────────────────────────
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    }

    private val rateLimitInterceptor = com.example.fotapp.data.network.RateLimitInterceptor()

    // ── Football-Data.org ───────────────────────────────────────────────────
    private val footballDataClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("X-Auth-Token", BuildConfig.FOOTBALL_API_KEY)
                    .build()
            )
        }
        .addInterceptor(rateLimitInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.football-data.org/v4/")
        .client(footballDataClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // ── api-sports.io (stats detalladas) ───────────────────────────────────
    private val apiSportsClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("x-apisports-key", BuildConfig.API_FOOTBALL_KEY)
                    .build()
            )
        }
        .addInterceptor(rateLimitInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiFootballRetrofit = Retrofit.Builder()
        .baseUrl("https://v3.football.api-sports.io/")
        .client(apiSportsClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiFootballService: ApiFootballService by lazy {
        apiFootballRetrofit.create(ApiFootballService::class.java)
    }

    // ── Free API Live Football Data (RapidAPI) ─────────────────────────────
    // La key se inyecta aquí como interceptor, NO en la interfaz
    private val rapidApiClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                    .addHeader("x-rapidapi-host", "free-api-live-football-data.p.rapidapi.com")
                    .build()
            )
        }
        .addInterceptor(rateLimitInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val rapidApiRetrofit = Retrofit.Builder()
        .baseUrl("https://free-api-live-football-data.p.rapidapi.com/")
        .client(rapidApiClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val rapidApiFootballService: RapidApiFootballService by lazy {
        rapidApiRetrofit.create(RapidApiFootballService::class.java)
    }

    // ── Repositorios ────────────────────────────────────────────────────────
    override val playerRepository: PlayerRepository by lazy {
        OfflineFirstPlayerRepository(
            apiService = apiService,
            apiFootballService = apiFootballService,
            rapidApiFootballService = rapidApiFootballService,
            playerDao = AppDatabase.getDatabase(context).playerDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
