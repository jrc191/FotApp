package com.example.fotapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fotapp.model.Comment

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerId: Int,
    val userName: String,
    val text: String,
    val date: String,
    val rating: Int
) {
    fun toComment(): Comment {
        return Comment(
            id = id,
            playerId = playerId,
            userName = userName,
            text = text,
            date = date,
            rating = rating
        )
    }
}

fun Comment.toEntity(): CommentEntity {
    return CommentEntity(
        id = id,
        playerId = playerId,
        userName = userName,
        text = text,
        date = date,
        rating = rating
    )
}
