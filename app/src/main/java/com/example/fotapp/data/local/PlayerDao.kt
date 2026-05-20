package com.example.fotapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE isFavorite = 1")
    fun getAllFavoritePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    suspend fun getPlayerById(id: Int): PlayerEntity?

    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun getPlayerByName(name: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Query("UPDATE players SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)
    
    @Query("DELETE FROM players WHERE id = :id AND isFavorite = 0")
    suspend fun deleteCachedPlayer(id: Int)

    @Query("SELECT * FROM comments WHERE playerId = :playerId")
    fun getCommentsForPlayer(playerId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    @Query("SELECT COUNT(*) FROM players WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM comments")
    fun getTotalCommentsCount(): Flow<Int>

    @Query("SELECT * FROM comments ORDER BY date DESC")
    fun getAllComments(): Flow<List<CommentEntity>>
    }
