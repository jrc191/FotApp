package com.example.fotapp.model

data class Player(
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
    val isFavorite: Boolean = false
)

data class Comment(
    val id: Int,
    val playerId: Int,
    val userName: String,
    val text: String,
    val date: String,
    val rating: Int
)