package app.rive.data.api

import app.rive.data.model.ProviderListResponse
import app.rive.data.model.StreamResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RiveScraperService {

    @GET("api/providers")
    suspend fun getProviders(): ProviderListResponse

    @GET("api/embeds")
    suspend fun getEmbedProviders(): ProviderListResponse

    @GET("api/provider")
    suspend fun getMovieStream(
        @Query("provider") provider: String,
        @Query("id") tmdbId: Long,
        @Query("cb") cacheBuster: Long = System.currentTimeMillis() / 3_000_000
    ): StreamResponse

    @GET("api/provider")
    suspend fun getTvStream(
        @Query("provider") provider: String,
        @Query("id") tmdbId: Long,
        @Query("season") season: Int,
        @Query("episode") episode: Int,
        @Query("cb") cacheBuster: Long = System.currentTimeMillis() / 3_000_000
    ): StreamResponse
}

