package com.example.fotapp.data.network

import com.example.fotapp.model.Player
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("287396c21008d3e9134a")
    suspend fun getPlayers(): List<Player>
}
