package com.example.fotapp.data.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()
        val responseString = when {
            uri.endsWith("players") -> PLAYERS_JSON
            else -> "[]"
        }

        return Response.Builder()
            .code(200)
            .message(responseString)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_0)
            .body(responseString.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }

    companion object {
        private const val PLAYERS_JSON = """
            [
                {
                    "id": 1,
                    "name": "Lionel Messi",
                    "position": "Delantero",
                    "team": "Inter Miami",
                    "nationality": "Argentina",
                    "age": 36,
                    "goals": 821,
                    "assists": 361,
                    "photo": "messi",
                    "description": "El mejor futbolista de todos los tiempos. 8 veces Balón de Oro."
                },
                {
                    "id": 2,
                    "name": "Cristiano Ronaldo",
                    "position": "Delantero",
                    "team": "Al Nassr",
                    "nationality": "Portugal",
                    "age": 38,
                    "goals": 850,
                    "assists": 268,
                    "photo": "ronaldo",
                    "description": "Uno de los máximos goleadores de la historia. 5 veces Balón de Oro."
                },
                {
                    "id": 3,
                    "name": "Kylian Mbappé",
                    "position": "Delantero",
                    "team": "PSG",
                    "nationality": "Francia",
                    "age": 25,
                    "goals": 287,
                    "assists": 134,
                    "photo": "mbappe",
                    "description": "Joven promesa del fútbol mundial. Campeón del Mundo 2018."
                },
                {
                    "id": 4,
                    "name": "Kevin De Bruyne",
                    "position": "Centrocampista",
                    "team": "Manchester City",
                    "nationality": "Bélgica",
                    "age": 32,
                    "goals": 156,
                    "assists": 258,
                    "photo": "debruyne",
                    "description": "Considerado uno de los mejores centrocampistas del mundo."
                },
                {
                    "id": 5,
                    "name": "Erling Haaland",
                    "position": "Delantero",
                    "team": "Manchester City",
                    "nationality": "Noruega",
                    "age": 23,
                    "goals": 215,
                    "assists": 54,
                    "photo": "haaland",
                    "description": "Goleador nato. Récord de goles en una temporada de Premier League."
                },
                {
                    "id": 6,
                    "name": "Vinicius Junior",
                    "position": "Delantero",
                    "team": "Real Madrid",
                    "nationality": "Brasil",
                    "age": 23,
                    "goals": 78,
                    "assists": 74,
                    "photo": "vinicius",
                    "description": "Extremo rápido y habilidoso. Clave en la Champions del Real Madrid."
                },
                {
                    "id": 7,
                    "name": "Robert Lewandowski",
                    "position": "Delantero",
                    "team": "Barcelona",
                    "nationality": "Polonia",
                    "age": 35,
                    "goals": 635,
                    "assists": 174,
                    "photo": "lewandowski",
                    "description": "Uno de los delanteros más letales de la última década."
                },
                {
                    "id": 8,
                    "name": "Mohamed Salah",
                    "position": "Delantero",
                    "team": "Liverpool",
                    "nationality": "Egipto",
                    "age": 31,
                    "goals": 315,
                    "assists": 133,
                    "photo": "salah",
                    "description": "Extremo veloz y goleador. Ídolo del Liverpool."
                },
                {
                    "id": 9,
                    "name": "Harry Kane",
                    "position": "Delantero",
                    "team": "Bayern Munich",
                    "nationality": "Inglaterra",
                    "age": 30,
                    "goals": 398,
                    "assists": 94,
                    "photo": "kane",
                    "description": "Delantero completo. Máximo goleador histórico de la selección inglesa."
                },
                {
                    "id": 10,
                    "name": "Luka Modric",
                    "position": "Centrocampista",
                    "team": "Real Madrid",
                    "nationality": "Croacia",
                    "age": 38,
                    "goals": 98,
                    "assists": 152,
                    "photo": "modric",
                    "description": "Mediocampista elegante. Balón de Oro 2018."
                }
            ]
        """
    }
}