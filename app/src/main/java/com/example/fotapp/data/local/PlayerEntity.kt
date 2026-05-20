package com.example.fotapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fotapp.model.Player

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val position: String,
    val team: String,
    val nationality: String,
    val age: Int,
    val goals: Int,
    val assists: Int,
    val photo: String,
    val description: String,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = 0L,
    // Stats detalladas
    val appearances: Int = 0,
    val rating: String = "N/A",
    val passAccuracy: Int = 0,
    val minutesPlayed: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val saves: Int = 0
) {
    fun toPlayer(): Player {
        return Player(
            id = id,
            name = name,
            position = position,
            team = team,
            nationality = nationality,
            age = age,
            goals = goals,
            assists = assists,
            photo = photo,
            description = description,
            isFavorite = isFavorite,
            lastUpdated = lastUpdated,
            appearances = appearances,
            rating = rating,
            passAccuracy = passAccuracy,
            minutesPlayed = minutesPlayed,
            yellowCards = yellowCards,
            redCards = redCards,
            saves = saves
        )
    }
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        name = name,
        position = position ?: "Jugador",
        team = team,
        nationality = nationality ?: "N/A",
        age = age,
        goals = goals,
        assists = assists,
        photo = photo,
        description = description,
        isFavorite = isFavorite,
        lastUpdated = lastUpdated,
        appearances = appearances,
        rating = rating,
        passAccuracy = passAccuracy,
        minutesPlayed = minutesPlayed,
        yellowCards = yellowCards,
        redCards = redCards,
        saves = saves
    )
}
