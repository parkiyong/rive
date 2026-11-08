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
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    movieRepository: MovieRepository,
    historyRepository: HistoryRepository,
    onMediaClick: (TmdbMediaItem) -> Unit,
    onPlayClick: (TmdbMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var heroItem by remember { mutableStateOf<TmdbMediaItem?>(null) }
    var trendingMovies by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var popularMovies by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var trendingTv by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var popularTv by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadFailed by remember { mutableStateOf(false) }

    val watchHistory by historyRepository.watchHistory.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    fun loadContent() {
        scope.launch {
            isLoading = true
            loadFailed = false
            try {
                movieRepository.getTrendingAll().onSuccess { items ->
                    heroItem = items.firstOrNull()
                }
                movieRepository.getTrendingMovies().onSuccess { trendingMovies = it }
                movieRepository.getPopularMovies().onSuccess { popularMovies = it }
                movieRepository.getTrendingTv().onSuccess { trendingTv = it }
                movieRepository.getPopularTv().onSuccess { popularTv = it }
            } catch (_: Exception) {
                loadFailed = true
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadContent()
    }

    if (isLoading && heroItem == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (!isLoading && heroItem == null && trendingMovies.isEmpty() && popularMovies.isEmpty()) {
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
                    onClick = { loadContent() },
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

        // Trending Movies
        MediaRow(
            title = "Trending Movies",
            items = trendingMovies,
            onItemClick = onMediaClick
        )

        // Trending TV Shows
        MediaRow(
            title = "Trending TV Shows",
            items = trendingTv,
            onItemClick = onMediaClick
        )

        // Popular Movies
        MediaRow(
            title = "Popular Movies",
            items = popularMovies,
            onItemClick = onMediaClick
        )

        // Popular TV Shows
        MediaRow(
            title = "Popular TV Shows",
            items = popularTv,
            onItemClick = onMediaClick
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

