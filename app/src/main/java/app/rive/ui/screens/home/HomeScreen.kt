package app.rive.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rive.data.model.TmdbMediaItem
import app.rive.data.repository.HistoryRepository
import app.rive.data.repository.MovieRepository
import app.rive.ui.components.HeroBanner
import app.rive.ui.components.MediaRow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    movieRepository: MovieRepository,
    historyRepository: HistoryRepository,
    onMediaClick: (TmdbMediaItem) -> Unit,
    onPlayClick: (TmdbMediaItem) -> Unit,
    category: String = "home",
    modifier: Modifier = Modifier
) {
    var heroItem by remember { mutableStateOf<TmdbMediaItem?>(null) }
    var trendingMovies by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var popularMovies by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var trendingTv by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var popularTv by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var topRatedMovies by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var topRatedTv by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    suspend fun loadContent() {
        isLoading = true
        try {
            coroutineScope {
                when (category) {
                    "movies" -> {
                        val trendingDef = async { movieRepository.getTrendingMovies() }
                        val popularDef = async { movieRepository.getPopularMovies() }
                        val topRatedDef = async { movieRepository.getTopRatedMovies() }

                        val trendingRes = trendingDef.await()
                        val popularRes = popularDef.await()
                        val topRatedRes = topRatedDef.await()

                        trendingRes.onSuccess { items ->
                            trendingMovies = items
                            heroItem = items.firstOrNull { it.backdropPath != null } ?: items.firstOrNull()
                        }
                        popularRes.onSuccess { popularMovies = it }
                        topRatedRes.onSuccess { topRatedMovies = it }
                    }
                    "tv" -> {
                        val trendingDef = async { movieRepository.getTrendingTv() }
                        val popularDef = async { movieRepository.getPopularTv() }
                        val topRatedDef = async { movieRepository.getTopRatedTv() }

                        val trendingRes = trendingDef.await()
                        val popularRes = popularDef.await()
                        val topRatedRes = topRatedDef.await()

                        trendingRes.onSuccess { items ->
                            trendingTv = items
                            heroItem = items.firstOrNull { it.backdropPath != null } ?: items.firstOrNull()
                        }
                        popularRes.onSuccess { popularTv = it }
                        topRatedRes.onSuccess { topRatedTv = it }
                    }
                    else -> { // "home"
                        val trendingAllDef = async { movieRepository.getTrendingAll() }
                        val trendingMoviesDef = async { movieRepository.getTrendingMovies() }
                        val popularMoviesDef = async { movieRepository.getPopularMovies() }
                        val trendingTvDef = async { movieRepository.getTrendingTv() }
                        val popularTvDef = async { movieRepository.getPopularTv() }

                        val trendingAllRes = trendingAllDef.await()
                        val trendingMoviesRes = trendingMoviesDef.await()
                        val popularMoviesRes = popularMoviesDef.await()
                        val trendingTvRes = trendingTvDef.await()
                        val popularTvRes = popularTvDef.await()

                        trendingAllRes.onSuccess { items -> heroItem = items.firstOrNull() }
                        trendingMoviesRes.onSuccess { trendingMovies = it }
                        popularMoviesRes.onSuccess { popularMovies = it }
                        trendingTvRes.onSuccess { trendingTv = it }
                        popularTvRes.onSuccess { popularTv = it }
                    }
                }
            }
        } catch (_: Exception) {
            // Handled via empty state check
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(category) {
        loadContent()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val hasContent = heroItem != null || trendingMovies.isNotEmpty() || popularMovies.isNotEmpty() ||
            trendingTv.isNotEmpty() || popularTv.isNotEmpty() || topRatedMovies.isNotEmpty() || topRatedTv.isNotEmpty()

    if (!hasContent) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Unable to load catalog",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Please check your internet connection or verify your TMDB API configuration in Settings.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { scope.launch { loadContent() } },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Retry", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        heroItem?.let { hero ->
            HeroBanner(
                item = hero,
                onPlayClick = { onPlayClick(hero) },
                onDetailClick = { onMediaClick(hero) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (category == "movies") {
            MediaRow(title = "Trending Movies", items = trendingMovies, onItemClick = onMediaClick)
            MediaRow(title = "Popular Movies", items = popularMovies, onItemClick = onMediaClick)
            MediaRow(title = "Top Rated Movies", items = topRatedMovies, onItemClick = onMediaClick)
        } else if (category == "tv") {
            MediaRow(title = "Trending TV Shows", items = trendingTv, onItemClick = onMediaClick)
            MediaRow(title = "Popular TV Shows", items = popularTv, onItemClick = onMediaClick)
            MediaRow(title = "Top Rated TV Shows", items = topRatedTv, onItemClick = onMediaClick)
        } else {
            MediaRow(title = "Trending Movies", items = trendingMovies, onItemClick = onMediaClick)
            MediaRow(title = "Trending TV Shows", items = trendingTv, onItemClick = onMediaClick)
            MediaRow(title = "Popular Movies", items = popularMovies, onItemClick = onMediaClick)
            MediaRow(title = "Popular TV Shows", items = popularTv, onItemClick = onMediaClick)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}


