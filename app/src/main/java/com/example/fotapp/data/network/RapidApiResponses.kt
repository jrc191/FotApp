package com.example.fotapp.data.network

import com.google.gson.annotations.SerializedName

// ── Búsqueda de jugador ────────────────────────────────────────────────────
data class RapidPlayerSearchResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("response") val response: RapidPlayerData?
)

data class RapidPlayerData(
    @SerializedName("players") val players: List<RapidPlayer>?,
    @SerializedName("suggestions") val suggestions: List<RapidPlayer>?
)

data class RapidPlayer(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("teamName") val teamName: String?,
    @SerializedName("position") val position: String?,
    @SerializedName("image") val imageUrl: String?,
    @SerializedName("cname") val nationality: String?
)

// ── Estadísticas del jugador ──────────────────────────────────────────────
data class RapidPlayerStatsResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("response") val response: RapidPlayerStatsData?
)

data class RapidPlayerStatsData(
    @SerializedName("statistics") val statistics: List<RapidStatItem>?
)

data class RapidStatItem(
    @SerializedName("team") val team: RapidTeamRef?,
    @SerializedName("league") val league: RapidLeagueRef?,
    @SerializedName("games") val games: RapidGameStats?,
    @SerializedName("goals") val goals: RapidGoalStats?,
    @SerializedName("assists") val assists: Int?,
    @SerializedName("passes") val passes: RapidPassStats?,
    @SerializedName("cards") val cards: RapidCardStats?,
    @SerializedName("shots") val shots: RapidShotStats?
)

data class RapidTeamRef(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("logo") val logo: String?
)

data class RapidLeagueRef(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("country") val country: String?
)

data class RapidGameStats(
    @SerializedName("appearences") val appearances: Int?,
    @SerializedName("lineups") val lineups: Int?,
    @SerializedName("minutes") val minutes: Int?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("position") val position: String?
)

data class RapidGoalStats(
    @SerializedName("total") val total: Int?,
    @SerializedName("conceded") val conceded: Int?,
    @SerializedName("assists") val assists: Int?,
    @SerializedName("saves") val saves: Int?
)

data class RapidPassStats(
    @SerializedName("total") val total: Int?,
    @SerializedName("key") val key: Int?,
    @SerializedName("accuracy") val accuracy: Int?
)

data class RapidCardStats(
    @SerializedName("yellow") val yellow: Int?,
    @SerializedName("red") val red: Int?
)

data class RapidShotStats(
    @SerializedName("total") val total: Int?,
    @SerializedName("on") val onTarget: Int?
)
