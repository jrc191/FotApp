package com.example.fotapp.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("teams/{id}")
    suspend fun getTeamSquad(
        @Path("id") teamId: Int
    ): TeamResponse

    @GET("areas")
    suspend fun getAreas(): AreaResponse

    @GET("competitions")
    suspend fun getCompetitions(
        @Query("areas") areaIds: String? = null
    ): CompetitionResponse

    @GET("competitions/{id}/teams")
    suspend fun getTeamsInCompetition(
        @Path("id") competitionId: Int
    ): TeamsInCompetitionResponse
}

interface ApiFootballService {
    @GET("players")
    suspend fun searchPlayer(
        @Query("search") name: String,
        @Query("season") season: Int = 2024,
        @Query("team") teamId: Int? = null,
        @Query("league") leagueId: Int? = null
    ): ApiFootballPlayerResponse

    @GET("teams")
    suspend fun getTeamByName(
        @Query("search") name: String
    ): ApiFootballTeamSearchResponse
}

// Los headers (x-rapidapi-key y x-rapidapi-host) se inyectan via interceptor en AppContainer
interface RapidApiFootballService {

    @GET("football-players-search/")
    suspend fun searchPlayer(
        @Query("search") name: String
    ): RapidPlayerSearchResponse

    @GET("football-player-statistics/")
    suspend fun getPlayerStatistics(
        @Query("playerId") playerId: String
    ): RapidPlayerStatsResponse
}
