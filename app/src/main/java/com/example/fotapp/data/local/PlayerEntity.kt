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
    val description: String
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
            isFavorite = true // If it's in DB, it's a favorite
        )
    }
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        name = name,
        position = position,
        team = team,
        nationality = nationality,
        age = age,
        goals = goals,
        assists = assists,
        photo = photo,
        description = description
    )
}
