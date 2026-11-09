package app.rive.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rive.data.model.TmdbEpisode
import app.rive.data.model.TmdbMediaDetail
import app.rive.data.model.TmdbMediaItem
import app.rive.data.repository.HistoryRepository
import app.rive.data.repository.MovieRepository
import app.rive.ui.components.MediaRow
import app.rive.ui.theme.StarGold
import coil.compose.AsyncImage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    tmdbId: Long,
    isTv: Boolean,
    movieRepository: MovieRepository,
    historyRepository: HistoryRepository,
    onBackClick: () -> Unit,
    onPlayMovie: (movieId: Long) -> Unit,
    onPlayEpisode: (tvId: Long, season: Int, episode: Int) -> Unit,
    onMediaClick: (TmdbMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var detail by remember { mutableStateOf<TmdbMediaDetail?>(null) }
    var episodes by remember { mutableStateOf<List<TmdbEpisode>>(emptyList()) }
    var recommendations by remember { mutableStateOf<List<TmdbMediaItem>>(emptyList()) }
    var selectedSeason by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }

    val isBookmarked by historyRepository.isBookmarked(tmdbId).collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    LaunchedEffect(tmdbId, isTv) {
        isLoading = true
        coroutineScope {
            if (isTv) {
                val detailsDef = async { movieRepository.getTvDetails(tmdbId) }
                val episodesDef = async { movieRepository.getTvSeasonEpisodes(tmdbId, selectedSeason) }
                val recsDef = async { movieRepository.getRecommendations(tmdbId, isTv = true) }

                detailsDef.await().onSuccess { detail = it }
                episodesDef.await().onSuccess { episodes = it }
                recsDef.await().onSuccess { recommendations = it }
            } else {
                val detailsDef = async { movieRepository.getMovieDetails(tmdbId) }
                val recsDef = async { movieRepository.getRecommendations(tmdbId, isTv = false) }

                detailsDef.await().onSuccess { detail = it }
                recsDef.await().onSuccess { recommendations = it }
            }
        }
        isLoading = false
    }

    LaunchedEffect(selectedSeason) {
        if (isTv) {
            movieRepository.getTvSeasonEpisodes(tmdbId, selectedSeason).onSuccess { episodes = it }
        }
    }

    if (isLoading && detail == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val item = detail ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Backdrop Header with Back and Bookmark buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            AsyncImage(
                model = item.backdropUrl,
                contentDescription = item.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            // Top action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                IconButton(
                    onClick = {
                        scope.launch {
                            historyRepository.toggleBookmark(
                                tmdbId = tmdbId,
                                mediaType = if (isTv) "tv" else "movie",
                                title = item.displayTitle,
                                posterPath = item.posterPath,
                                backdropPath = item.backdropPath,
                                voteAverage = item.voteAverage,
                                releaseYear = item.releaseDate ?: item.firstAirDate,
                                isCurrentlyBookmarked = isBookmarked
                            )
                        }
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
        }

        // Details Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = item.displayTitle,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!item.tagline.isNullOrBlank()) {
                Text(
                    text = item.tagline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata info badges (Rating, Year, Runtime/Seasons)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = StarGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", item.voteAverage),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                (item.releaseDate ?: item.firstAirDate)?.take(4)?.let { year ->
                    Text(text = year, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (isTv) {
                    item.numberOfSeasons?.let { seasons ->
                        Text(text = "$seasons Seasons", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    item.runtime?.let { mins ->
                        Text(text = "${mins}m", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Play Button for Movies
            if (!isTv) {
                Button(
                    onClick = { onPlayMovie(tmdbId) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Play Movie", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Overview text
            if (!item.overview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            // TV Season and Episode Selector
            if (isTv && item.seasons != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Seasons & Episodes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Season Selector Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(item.seasons.filter { it.seasonNumber > 0 }) { season ->
                        FilterChip(
                            selected = season.seasonNumber == selectedSeason,
                            onClick = { selectedSeason = season.seasonNumber },
                            label = { Text("Season ${season.seasonNumber}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Episodes list
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    episodes.forEach { ep ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onPlayEpisode(tmdbId, selectedSeason, ep.episodeNumber) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = ep.stillUrl,
                                    contentDescription = ep.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "E${ep.episodeNumber}: ${ep.name}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (!ep.overview.isNullOrBlank()) {
                                    Text(
                                        text = ep.overview,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recommendations
        if (recommendations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            MediaRow(
                title = "You Might Also Like",
                items = recommendations,
                onItemClick = onMediaClick
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

