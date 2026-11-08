package app.rive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val id: String, // "movie_{tmdbId}" or "tv_{tmdbId}_{season}_{episode}"
    val tmdbId: Long,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodeTitle: String? = null,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val progressPercentage: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: Long, // tmdbId
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseYear: String?,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

