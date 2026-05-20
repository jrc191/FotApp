package com.example.fotapp.data.network

import com.example.fotapp.model.Player
import java.util.Calendar

data class TeamResponse(
    val id: Int,
    val name: String,
    val crest: String,
    val squad: List<SquadMember>
)

data class SquadMember(
    val id: Int,
    val name: String,
    val position: String?,
    val dateOfBirth: String?,
    val nationality: String?
) {
    fun toPlayer(teamName: String, teamCrest: String): Player {
        val age = dateOfBirth?.let {
            try {
                val parts = it.split("-")
                if (parts.size >= 1) {
                    val birthYear = parts[0].toInt()
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    currentYear - birthYear
                } else 25
            } catch (e: Exception) {
                25
            }
        } ?: 25

        return Player(
            id = id,
            name = name,
            position = position ?: "Jugador",
            team = teamName,
            nationality = nationality ?: "N/A",
            age = age,
            goals = (0..20).random(),
            assists = (0..15).random(),
            photo = teamCrest,
            description = "Jugador profesional de $teamName. Nacionalidad: $nationality.",
            isFavorite = false
        )
    }
}
