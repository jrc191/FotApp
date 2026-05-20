package com.example.fotapp.data.network

data class ApiFootballPlayerResponse(
    val response: List<PlayerProfile>
)

data class PlayerProfile(
    val player: PlayerBasicInfo,
    val statistics: List<PlayerStats>
)

data class PlayerBasicInfo(
    val id: Int,
    val name: String,
    val firstname: String?,
    val lastname: String?,
    val age: Int?,
    val nationality: String?,
    val photo: String?
)

data class PlayerStats(
    val team: TeamBasicInfo,
    val goals: GoalStats,
    val passes: PassStats,
    val games: GameStats
)

data class ApiFootballTeamSearchResponse(
    val response: List<TeamWrapper>
)

data class TeamWrapper(
    val team: TeamBasicInfo,
    val venue: VenueInfo?
)

data class VenueInfo(
    val id: Int?,
    val name: String?,
    val city: String?
)

data class TeamBasicInfo(
    val id: Int,
    val name: String,
    val logo: String
)

data class GoalStats(
    val total: Int?,
    val assists: Int?,
    val conceded: Int?,
    val saves: Int?
)

data class PassStats(
    val total: Int?,
    val accuracy: Int?
)

data class GameStats(
    val position: String?,
    val rating: String?,
    val appearences: Int?
)
