package app.rive.data.repository

import app.rive.BuildConfig
import app.rive.data.api.ApiClient
import app.rive.data.model.TmdbEpisode
import app.rive.data.model.TmdbMediaDetail
import app.rive.data.model.TmdbMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(
    private var apiKey: String = BuildConfig.DEFAULT_TMDB_API_KEY
) {
    private val tmdbService = ApiClient.tmdbService

    fun updateApiKey(newKey: String) {
        if (newKey.isNotBlank()) {
            apiKey = newKey
        }
    }

    suspend fun getTrendingAll(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTrendingAll(apiKey, page).results.filter { it.backdropPath != null }
        }
    }

    suspend fun getTrendingMovies(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTrendingMovies(apiKey, page).results
        }
    }

    suspend fun getTrendingTv(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTrendingTv(apiKey, page).results
        }
    }

    suspend fun getPopularMovies(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getPopularMovies(apiKey, page).results
        }
    }

    suspend fun getTopRatedMovies(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTopRatedMovies(apiKey, page).results
        }
    }

    suspend fun getPopularTv(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getPopularTv(apiKey, page).results
        }
    }

    suspend fun getTopRatedTv(page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTopRatedTv(apiKey, page).results
        }
    }

    suspend fun search(query: String, page: Int = 1): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.searchMulti(apiKey, query, page).results.filter {
                it.mediaType == "movie" || it.mediaType == "tv"
            }
        }
    }

    suspend fun getMovieDetails(movieId: Long): Result<TmdbMediaDetail> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getMovieDetails(movieId, apiKey)
        }
    }

    suspend fun getTvDetails(tvId: Long): Result<TmdbMediaDetail> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTvDetails(tvId, apiKey)
        }
    }

    suspend fun getTvSeasonEpisodes(tvId: Long, seasonNumber: Int): Result<List<TmdbEpisode>> = withContext(Dispatchers.IO) {
        runCatching {
            tmdbService.getTvSeason(tvId, seasonNumber, apiKey).episodes
        }
    }

    suspend fun getRecommendations(id: Long, isTv: Boolean): Result<List<TmdbMediaItem>> = withContext(Dispatchers.IO) {
        runCatching {
            if (isTv) {
                tmdbService.getTvRecommendations(id, apiKey).results
            } else {
                tmdbService.getMovieRecommendations(id, apiKey).results
            }
        }
    }
}

