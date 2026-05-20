package com.example.fotapp.data.network

data class AreaResponse(
    val areas: List<Area>
)

data class Area(
    val id: Int,
    val name: String,
    val countryCode: String?,
    val flag: String?
)

data class CompetitionResponse(
    val competitions: List<Competition>
)

data class Competition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String?
)

data class TeamsInCompetitionResponse(
    val teams: List<TeamInfo>
)

data class TeamInfo(
    val id: Int,
    val name: String,
    val crest: String
)
