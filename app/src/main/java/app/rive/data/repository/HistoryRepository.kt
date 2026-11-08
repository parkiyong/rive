package app.rive.data.repository

import app.rive.data.local.BookmarkDao
import app.rive.data.local.BookmarkEntity
import app.rive.data.local.HistoryDao
import app.rive.data.local.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val bookmarkDao: BookmarkDao
) {
    val watchHistory: Flow<List<WatchHistoryEntity>> = historyDao.getAllHistory()
    val bookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    suspend fun saveProgress(
        tmdbId: Long,
        mediaType: String,
        title: String,
        posterPath: String?,
        backdropPath: String?,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null,
        episodeTitle: String? = null,
        positionMs: Long,
        durationMs: Long
    ) {
        val id = if (mediaType == "tv" && seasonNumber != null && episodeNumber != null) {
            "tv_${tmdbId}_${seasonNumber}_${episodeNumber}"
        } else {
            "movie_${tmdbId}"
        }

        historyDao.upsertHistory(
            WatchHistoryEntity(
                id = id,
                tmdbId = tmdbId,
                mediaType = mediaType,
                title = title,
                posterPath = posterPath,
                backdropPath = backdropPath,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
                episodeTitle = episodeTitle,
                positionMs = positionMs,
                durationMs = durationMs
            )
        )
    }

    suspend fun getSavedPosition(tmdbId: Long, seasonNumber: Int? = null, episodeNumber: Int? = null): Long {
        val id = if (seasonNumber != null && episodeNumber != null) {
            "tv_${tmdbId}_${seasonNumber}_${episodeNumber}"
        } else {
            "movie_${tmdbId}"
        }
        return historyDao.getHistoryItem(id)?.positionMs ?: 0L
    }

    fun isBookmarked(tmdbId: Long): Flow<Boolean> = bookmarkDao.isBookmarked(tmdbId)

    suspend fun toggleBookmark(
        tmdbId: Long,
        mediaType: String,
        title: String,
        posterPath: String?,
        backdropPath: String?,
        voteAverage: Double,
        releaseYear: String?,
        isCurrentlyBookmarked: Boolean
    ) {
        if (isCurrentlyBookmarked) {
            bookmarkDao.removeBookmark(tmdbId)
        } else {
            bookmarkDao.addBookmark(
                BookmarkEntity(
                    id = tmdbId,
                    mediaType = mediaType,
                    title = title,
                    posterPath = posterPath,
                    backdropPath = backdropPath,
                    voteAverage = voteAverage,
                    releaseYear = releaseYear
                )
            )
        }
    }
}

