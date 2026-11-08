package app.rive.data.model

import com.google.gson.annotations.SerializedName

data class TmdbPageResponse<T>(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<T>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TmdbMediaItem(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("genre_ids") val genreIds: List<Int>? = emptyList()
) {
    val displayTitle: String
        get() = title ?: name ?: "Untitled"

    val releaseYear: String
        get() = (releaseDate ?: firstAirDate)?.take(4) ?: ""

    val posterUrl: String
        get() = if (!posterPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/w500$posterPath" else ""

    val backdropUrl: String
        get() = if (!backdropPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/original$backdropPath" else ""

    val isTvShow: Boolean
        get() = mediaType == "tv" || (!name.isNullOrEmpty() && title.isNullOrEmpty())
}

data class TmdbMediaDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genres") val genres: List<TmdbGenre>?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int?,
    @SerializedName("seasons") val seasons: List<TmdbSeasonSummary>?
) {
    val displayTitle: String
        get() = title ?: name ?: "Untitled"

    val posterUrl: String
        get() = if (!posterPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/w500$posterPath" else ""

    val backdropUrl: String
        get() = if (!backdropPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/original$backdropPath" else ""
}

data class TmdbGenre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class TmdbSeasonSummary(
    @SerializedName("id") val id: Long,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("poster_path") val posterPath: String?
)

data class TmdbSeasonDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String,
    @SerializedName("episodes") val episodes: List<TmdbEpisode>
)

data class TmdbEpisode(
    @SerializedName("id") val id: Long,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("vote_average") val voteAverage: Double
) {
    val stillUrl: String
        get() = if (!stillPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/w500$stillPath" else ""
}

