package com.example.fotapp.model

import com.google.gson.annotations.SerializedName

data class Player(
    val id: Int,
    val name: String,
    val position: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    val nationality: String? = null,
    @SerializedName("section") val section: String? = null,
    @SerializedName("shirtNumber") val shirtNumber: Int? = null,
    val team: String = "N/A",
    val photo: String = "https://crests.football-data.org/764.svg",
    val description: String = "Jugador profesional de fútbol.",
    val goals: Int = 0,
    val assists: Int = 0,
    val age: Int = 0,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = 0L,
    // Stats detalladas
    val appearances: Int = 0,
    val rating: String = "N/A",
    val passAccuracy: Int = 0,
    val minutesPlayed: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val saves: Int = 0        // Para porteros
)

data class Comment(
    val id: Int,
    val playerId: Int,
    val userName: String,
    val text: String,
    val date: String,
    val rating: Int
)
