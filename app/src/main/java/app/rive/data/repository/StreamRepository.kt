package app.rive.data.repository

import app.rive.data.api.ApiClient
import app.rive.data.model.StreamData
import app.rive.data.model.StreamSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreamRepository {

    private val scraperService = ApiClient.scraperService

    // Prioritized list of high-reliability Rive scrapers
    private val preferredProviders = listOf(
        "citadel",
        "primevids",
        "apollo",
        "kismet",
        "vanguard",
        "apogee",
        "lumen",
        "mirage",
        "prometheus",
        "fortress"
    )

    suspend fun resolveMovieStream(tmdbId: Long): Result<StreamData> = withContext(Dispatchers.IO) {
        runCatching {
            // First try high-priority providers sequentially
            for (provider in preferredProviders) {
                try {
                    val response = scraperService.getMovieStream(provider = provider, tmdbId = tmdbId)
                    val data = response.data
                    if (data != null && !data.sources.isNullOrEmpty()) {
                        return@runCatching data
                    }
                } catch (_: Exception) {
                    // Try next provider
                }
            }

            // Fallback: fetch dynamic provider list from API and try any remaining
            val remoteProviders = try {
                scraperService.getProviders().data.filter { it !in preferredProviders }
            } catch (_: Exception) {
                emptyList()
            }

            for (provider in remoteProviders) {
                try {
                    val response = scraperService.getMovieStream(provider = provider, tmdbId = tmdbId)
                    val data = response.data
                    if (data != null && !data.sources.isNullOrEmpty()) {
                        return@runCatching data
                    }
                } catch (_: Exception) {
                    // Continue
                }
            }

            throw IllegalStateException("No working stream sources found for movie $tmdbId")
        }
    }

    suspend fun resolveTvStream(tmdbId: Long, season: Int, episode: Int): Result<StreamData> = withContext(Dispatchers.IO) {
        runCatching {
            for (provider in preferredProviders) {
                try {
                    val response = scraperService.getTvStream(
                        provider = provider,
                        tmdbId = tmdbId,
                        season = season,
                        episode = episode
                    )
                    val data = response.data
                    if (data != null && !data.sources.isNullOrEmpty()) {
                        return@runCatching data
                    }
                } catch (_: Exception) {
                    // Continue to next provider
                }
            }

            throw IllegalStateException("No working stream sources found for S${season}E${episode}")
        }
    }
}

