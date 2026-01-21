package com.example.fotapp.data

import com.example.fotapp.R
import com.example.fotapp.model.Player

object Datasource {

    val playersList = listOf(
        Player(
            id = 1,
            name = "Lionel Messi",
            position = "Delantero",
            team = "Inter Miami",
            nationality = "Argentina",
            age = 36,
            goals = 821,
            assists = 361,
            photo = "messi",
            description = "El mejor futbolista de todos los tiempos. 8 veces Balón de Oro."
        ),
        Player(
            id = 2,
            name = "Cristiano Ronaldo",
            position = "Delantero",
            team = "Al Nassr",
            nationality = "Portugal",
            age = 38,
            goals = 850,
            assists = 268,
            photo = "ronaldo",
            description = "Uno de los máximos goleadores de la historia. 5 veces Balón de Oro."
        ),
        Player(
            id = 3,
            name = "Kylian Mbappé",
            position = "Delantero",
            team = "PSG",
            nationality = "Francia",
            age = 25,
            goals = 287,
            assists = 134,
            photo = "mbappe",
            description = "Joven promesa del fútbol mundial. Campeón del Mundo 2018."
        ),
        Player(
            id = 4,
            name = "Kevin De Bruyne",
            position = "Centrocampista",
            team = "Manchester City",
            nationality = "Bélgica",
            age = 32,
            goals = 156,
            assists = 258,
            photo = "debruyne",
            description = "Considerado uno de los mejores centrocampistas del mundo."
        ),
        Player(
            id = 5,
            name = "Erling Haaland",
            position = "Delantero",
            team = "Manchester City",
            nationality = "Noruega",
            age = 23,
            goals = 215,
            assists = 54,
            photo = "haaland",
            description = "Goleador nato. Récord de goles en una temporada de Premier League."
        ),
        Player(
            id = 6,
            name = "Vinicius Junior",
            position = "Delantero",
            team = "Real Madrid",
            nationality = "Brasil",
            age = 23,
            goals = 78,
            assists = 74,
            photo = "vinicius",
            description = "Extremo rápido y habilidoso. Clave en la Champions del Real Madrid."
        ),
        Player(
            id = 7,
            name = "Robert Lewandowski",
            position = "Delantero",
            team = "Barcelona",
            nationality = "Polonia",
            age = 35,
            goals = 635,
            assists = 174,
            photo = "lewandowski",
            description = "Uno de los delanteros más letales de la última década."
        ),
        Player(
            id = 8,
            name = "Mohamed Salah",
            position = "Delantero",
            team = "Liverpool",
            nationality = "Egipto",
            age = 31,
            goals = 315,
            assists = 133,
            photo = "salah",
            description = "Extremo veloz y goleador. Ídolo del Liverpool."
        ),
        Player(
            id = 9,
            name = "Harry Kane",
            position = "Delantero",
            team = "Bayern Munich",
            nationality = "Inglaterra",
            age = 30,
            goals = 398,
            assists = 94,
            photo = "kane",
            description = "Delantero completo. Máximo goleador histórico de la selección inglesa."
        ),
        Player(
            id = 10,
            name = "Luka Modric",
            position = "Centrocampista",
            team = "Real Madrid",
            nationality = "Croacia",
            age = 38,
            goals = 98,
            assists = 152,
            photo = "modric",
            description = "Mediocampista elegante. Balón de Oro 2018."
        )
    )

    fun getPlayerById(id: Int): Player? {
        return playersList.find { it.id == id }
    }

    // NUEVO: Requerido para buscar detalles por nombre
    fun getPlayerByName(name: String): Player? {
        return playersList.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getPlayersByTeam(team: String): List<Player> {
        return playersList.filter { it.team == team }
    }

    fun getDrawableIdByName(name: String): Int {
        return when (name.lowercase()) {
            "messi" -> R.drawable.messi
            "ronaldo" -> R.drawable.ronaldo
            "mbappe" -> R.drawable.mbappe
            "debruyne" -> R.drawable.debruyne
            "haaland" -> R.drawable.haaland
            "vinicius" -> R.drawable.eufarei
            "lewandowski" -> R.drawable.lewandowski
            "salah" -> R.drawable.salah
            "kane" -> R.drawable.kane
            "modric" -> R.drawable.modric
            else -> R.drawable.ic_futconnect
        }
    }

    fun getFlagEmoji(nationality: String): String {
        return when (nationality.lowercase()) {
            "argentina" -> "🇦🇷"
            "portugal" -> "🇵🇹"
            "francia" -> "🇫🇷"
            "bélgica" -> "🇧🇪"
            "noruega" -> "🇳🇴"
            "brasil" -> "🇧🇷"
            "polonia" -> "🇵🇱"
            "egipto" -> "🇪🇬"
            "inglaterra" -> "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
            "croacia" -> "🇭🇷"
            "españa" -> "🇪🇸"
            "alemania" -> "🇩🇪"
            else -> "⚽"
        }
    }
}