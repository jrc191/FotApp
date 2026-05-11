package com.example.fotapp.data.repository

import com.example.fotapp.data.local.PlayerDao
import com.example.fotapp.data.local.toEntity
import com.example.fotapp.data.network.ApiService
import com.example.fotapp.model.Comment
import com.example.fotapp.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlayerRepository {
    suspend fun getPlayersFromApi(): List<Player>
    fun getFavoritePlayersStream(): Flow<List<Player>>
    suspend fun savePlayerToFavorites(player: Player)
    suspend fun removePlayerFromFavorites(player: Player)
    fun getCommentsForPlayer(playerId: Int): Flow<List<Comment>>
    suspend fun addComment(comment: Comment)
}

class OfflineFirstPlayerRepository(
    private val apiService: ApiService,
    private val playerDao: PlayerDao
) : PlayerRepository {

    override suspend fun getPlayersFromApi(): List<Player> {
        return apiService.getPlayers()
    }

    override fun getFavoritePlayersStream(): Flow<List<Player>> {
        return playerDao.getAllFavoritePlayers().map { entities ->
            entities.map { it.toPlayer() }
        }
    }

    override suspend fun savePlayerToFavorites(player: Player) {
        playerDao.insertPlayer(player.toEntity())
    }

    override suspend fun removePlayerFromFavorites(player: Player) {
        playerDao.deletePlayer(player.toEntity())
    }

    override fun getCommentsForPlayer(playerId: Int): Flow<List<Comment>> {
        return playerDao.getCommentsForPlayer(playerId).map { entities ->
            entities.map { it.toComment() }
        }
    }

    override suspend fun addComment(comment: Comment) {
        playerDao.insertComment(comment.toEntity())
    }
}
