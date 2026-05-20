package com.example.fotapp.data.repository

import com.example.fotapp.data.local.PlayerDao
import com.example.fotapp.data.local.toEntity
import com.example.fotapp.data.network.ApiFootballService
import com.example.fotapp.data.network.ApiService
import com.example.fotapp.data.network.Area
import com.example.fotapp.data.network.Competition
import com.example.fotapp.data.network.RapidApiFootballService
import com.example.fotapp.data.network.TeamInfo
import com.example.fotapp.model.Comment
import com.example.fotapp.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlayerRepository {
    suspend fun getPlayersWithRealStats(teamId: Int? = null): List<Player>
    suspend fun getPlayerDetailedStats(name: String, forceRefresh: Boolean = false): Player?
    suspend fun refreshPlayerStats(player: Player): Player?
    suspend fun getAreas(): List<Area>
    suspend fun getCompetitions(areaId: Int?): List<Competition>
    suspend fun getTeams(competitionId: Int): List<TeamInfo>
    fun getFavoritePlayersStream(): Flow<List<Player>>
    suspend fun savePlayerToFavorites(player: Player)
    suspend fun removePlayerFromFavorites(player: Player)
    fun getCommentsForPlayer(playerId: Int): Flow<List<Comment>>
    fun getAllComments(): Flow<List<Comment>>
    suspend fun addComment(comment: Comment)
    suspend fun updateComment(comment: Comment)
    suspend fun deleteComment(comment: Comment)
    fun getFavoriteCount(): Flow<Int>
    fun getTotalCommentsCount(): Flow<Int>
}

class OfflineFirstPlayerRepository(
    private val apiService: ApiService,
    private val apiFootballService: ApiFootballService,
    private val rapidApiFootballService: RapidApiFootballService,
    private val playerDao: PlayerDao
) : PlayerRepository {

    companion object {
        /** Caché válida durante 24 horas — evita llamadas innecesarias a la API */
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L
        private const val DEFAULT_PHOTO = "https://crests.football-data.org/764.svg"
    }

    /** Caché en memoria para la sesión activa */
    private val sessionCache = mutableMapOf<String, Player>()

    // ── Lista de jugadores (solo info básica, sin stats) ───────────────────

    override suspend fun getPlayersWithRealStats(teamId: Int?): List<Player> {
        if (teamId == null) return emptyList()

        val response = apiService.getTeamSquad(teamId)
        return response.squad.map { member ->
            // Comprobamos caché local antes de devolver el jugador sin stats
            playerDao.getPlayerByName(member.name)?.toPlayer()
                ?: member.toPlayer(response.name, response.crest)
        }
    }

    // ── Stats detalladas (con caché de 24 h) ──────────────────────────────

    override suspend fun getPlayerDetailedStats(name: String, forceRefresh: Boolean): Player? {
        if (!forceRefresh) {
            // 1. Caché en memoria (sesión activa)
            val cached = sessionCache[name]
            if (cached != null && isFresh(cached.lastUpdated)) return cached

            // 2. Caché persistente en Room
            val local = playerDao.getPlayerByName(name)
            if (local != null && isFresh(local.lastUpdated)) {
                return local.toPlayer().also { sessionCache[name] = it }
            }
        }

        // 3. Petición real a la API
        return fetchFromApi(name, preferredTeam = null)
    }

    override suspend fun refreshPlayerStats(player: Player): Player? {
        // Siempre ignora la caché (refresh manual)
        return fetchFromApi(player.name, preferredTeam = player.team)
    }

    // ── Lógica de fetching ────────────────────────────────────────────────

    private suspend fun fetchFromApi(name: String, preferredTeam: String?): Player? {
        val normalizedName = normalizeName(name)
        
        // — Paso 1: RapidAPI → foto + id del jugador ——————————————————
        val rapidPlayer = try {
            val rapidResponse = rapidApiFootballService.searchPlayer(name)
            val list = rapidResponse.response?.players ?: rapidResponse.response?.suggestions
            list?.firstOrNull { it.name?.contains(name, ignoreCase = true) == true }
                ?: list?.firstOrNull()
        } catch (e: Exception) {
            android.util.Log.w("PlayerRepository", "RapidAPI search failed: ${e.message}")
            null
        }

        // — Paso 2: Stats desde RapidAPI si tenemos ID ————————————————
        var rapidStats: com.example.fotapp.data.network.RapidStatItem? = null
        if (rapidPlayer?.id != null) {
            try {
                val statsResponse = rapidApiFootballService.getPlayerStatistics(rapidPlayer.id)
                rapidStats = statsResponse.response?.statistics
                    ?.maxByOrNull { it.games?.appearances ?: 0 }
            } catch (e: Exception) {
                android.util.Log.w("PlayerRepository", "RapidAPI stats failed: ${e.message}")
            }
        }

        // — Paso 3: Stats desde api-sports.io (fallback o complemento) —
        val teamName = preferredTeam ?: rapidPlayer?.teamName
        var apiSportsTeamId: Int? = null
        if (teamName != null) {
            try {
                apiSportsTeamId = apiFootballService.getTeamByName(normalizeName(teamName))
                    .response.firstOrNull()?.team?.id
            } catch (_: Exception) { }
        }

        val profile = try {
            // API-Football requiere Team o League si se usa 'search'
            if (apiSportsTeamId != null) {
                apiFootballService.searchPlayer(name = normalizedName, teamId = apiSportsTeamId)
                    .response
                    .find { normalizeName(it.player.name).contains(normalizedName, ignoreCase = true) }
            } else {
                // Si no hay equipo, no podemos usar 'search' en API-Football (daría error 200 con body de error)
                null
            }
        } catch (e: Exception) {
            android.util.Log.w("PlayerRepository", "ApiSports search failed: ${e.message}")
            null
        }

        // — Paso 4: Construir el modelo Player ————————————————————————
        val existing = if (profile != null)
            playerDao.getPlayerById(profile.player.id)
        else
            playerDao.getPlayerByName(rapidPlayer?.name ?: name)

        val player: Player? = when {
            profile != null -> {
                val stats = profile.statistics
                val mainStats = stats.maxByOrNull { it.games.appearences ?: 0 } ?: stats.first()
                val isGK = mainStats.games.position?.contains("Goalkeeper", ignoreCase = true) == true

                // Priorizamos rapidStats si disponibles; si no, api-sports
                val goals = rapidStats?.goals?.total
                    ?: stats.sumOf { it.goals.total ?: 0 }
                val assists = rapidStats?.goals?.assists
                    ?: stats.sumOf { it.goals.assists ?: 0 }
                val appearances = rapidStats?.games?.appearances
                    ?: stats.sumOf { it.games.appearences ?: 0 }
                val rating = rapidStats?.games?.rating
                    ?: mainStats.games.rating
                    ?: "N/A"
                val passAccuracy = rapidStats?.passes?.accuracy
                    ?: mainStats.passes.accuracy
                    ?: 0
                val minutesPlayed = rapidStats?.games?.minutes
                    ?: 0
                val yellowCards = rapidStats?.cards?.yellow ?: 0
                val redCards = rapidStats?.cards?.red ?: 0
                val saves = rapidStats?.goals?.saves
                    ?: if (isGK) stats.sumOf { it.goals.saves ?: 0 } else 0

                val description = "" // La descripción se generará en la UI para evitar literales en el código

                Player(
                    id = profile.player.id,
                    name = profile.player.name,
                    position = if (isGK) "Portero" else (mainStats.games.position ?: rapidPlayer?.position ?: "Jugador"),
                    nationality = profile.player.nationality ?: rapidPlayer?.nationality ?: "N/A",
                    age = profile.player.age ?: 0,
                    goals = goals,
                    assists = assists,
                    photo = rapidPlayer?.imageUrl ?: profile.player.photo ?: DEFAULT_PHOTO,
                    team = mainStats.team.name,
                    description = description,
                    isFavorite = existing?.isFavorite ?: false,
                    lastUpdated = System.currentTimeMillis(),
                    appearances = appearances,
                    rating = rating,
                    passAccuracy = passAccuracy,
                    minutesPlayed = minutesPlayed,
                    yellowCards = yellowCards,
                    redCards = redCards,
                    saves = saves
                )
            }

            rapidPlayer != null -> {
                // Solo tenemos datos de RapidAPI
                Player(
                    id = rapidPlayer.id?.toIntOrNull() ?: existing?.id ?: 0,
                    name = rapidPlayer.name ?: name,
                    position = rapidPlayer.position ?: "Jugador",
                    nationality = rapidPlayer.nationality ?: "N/A",
                    age = 0,
                    goals = rapidStats?.goals?.total ?: 0,
                    assists = rapidStats?.goals?.assists ?: 0,
                    photo = rapidPlayer.imageUrl ?: DEFAULT_PHOTO,
                    team = rapidPlayer.teamName ?: "N/A",
                    description = "",
                    isFavorite = existing?.isFavorite ?: false,
                    lastUpdated = System.currentTimeMillis(),
                    appearances = rapidStats?.games?.appearances ?: 0,
                    rating = rapidStats?.games?.rating ?: "N/A",
                    passAccuracy = rapidStats?.passes?.accuracy ?: 0,
                    minutesPlayed = rapidStats?.games?.minutes ?: 0,
                    yellowCards = rapidStats?.cards?.yellow ?: 0,
                    redCards = rapidStats?.cards?.red ?: 0,
                    saves = rapidStats?.goals?.saves ?: 0
                )
            }

            else -> null
        }

        // — Paso 5: Guardar en Room y caché de sesión —————————————————
        player?.let {
            playerDao.insertPlayer(it.toEntity())
            sessionCache[name] = it
        }
        return player
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun isFresh(lastUpdated: Long): Boolean =
        lastUpdated > 0L && System.currentTimeMillis() - lastUpdated < CACHE_TTL_MS

    private fun normalizeName(name: String): String {
        val temp = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
        return temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .replace("[^\\p{Alnum} ]".toRegex(), "") // Solo alfanuméricos y espacios según la API
    }

    // ── Filtros (Football-Data.org) ───────────────────────────────────────

    override suspend fun getAreas(): List<Area> =
        apiService.getAreas().areas.filter { it.countryCode != null }.sortedBy { it.name }

    override suspend fun getCompetitions(areaId: Int?): List<Competition> =
        apiService.getCompetitions(areaId?.toString()).competitions.sortedBy { it.name }

    override suspend fun getTeams(competitionId: Int): List<TeamInfo> =
        apiService.getTeamsInCompetition(competitionId).teams.sortedBy { it.name }

    // ── Favoritos ────────────────────────────────────────────────────────

    override fun getFavoritePlayersStream(): Flow<List<Player>> =
        playerDao.getAllFavoritePlayers().map { it.map { e -> e.toPlayer() } }

    override suspend fun savePlayerToFavorites(player: Player) {
        val inDb = playerDao.getPlayerById(player.id)
        if (inDb == null) {
            playerDao.insertPlayer(player.copy(isFavorite = true).toEntity())
        } else {
            playerDao.updateFavoriteStatus(player.id, true)
        }
    }

    override suspend fun removePlayerFromFavorites(player: Player) {
        playerDao.updateFavoriteStatus(player.id, false)
    }

    // ── Comentarios ───────────────────────────────────────────────────────

    override fun getCommentsForPlayer(playerId: Int): Flow<List<Comment>> =
        playerDao.getCommentsForPlayer(playerId).map { entities -> entities.map { it.toComment() } }

    override fun getAllComments(): Flow<List<Comment>> =
        playerDao.getAllComments().map { entities -> entities.map { it.toComment() } }

    override suspend fun addComment(comment: Comment) = playerDao.insertComment(comment.toEntity())
    override suspend fun updateComment(comment: Comment) = playerDao.insertComment(comment.toEntity())
    override suspend fun deleteComment(comment: Comment) = playerDao.deleteComment(comment.toEntity())

    override fun getFavoriteCount(): Flow<Int> = playerDao.getFavoriteCount()
    override fun getTotalCommentsCount(): Flow<Int> = playerDao.getTotalCommentsCount()
}
