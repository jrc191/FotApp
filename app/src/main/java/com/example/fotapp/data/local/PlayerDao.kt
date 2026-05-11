package com.example.fotapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players")
    fun getAllFavoritePlayers(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("SELECT * FROM comments WHERE playerId = :playerId")
    fun getCommentsForPlayer(playerId: Int): Flow<List<CommentEntity>>

    @Insert
    suspend fun insertComment(comment: CommentEntity)
}
