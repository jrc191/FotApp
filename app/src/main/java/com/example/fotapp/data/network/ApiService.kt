package com.example.fotapp.data.network

import com.example.fotapp.model.Player
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("players")
    suspend fun getPlayers(): List<Player>

    @GET("players/{id}")
    suspend fun getPlayerById(@Path("id") id: Int): Player
}
