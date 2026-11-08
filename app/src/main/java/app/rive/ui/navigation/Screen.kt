package app.rive.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Movies : Screen("movies", "Movies", Icons.Default.Movie)
    object TvShows : Screen("tv", "TV Shows", Icons.Default.Tv)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    object Detail : Screen("detail/{tmdbId}/{isTv}", "Detail") {
        fun createRoute(tmdbId: Long, isTv: Boolean): String = "detail/$tmdbId/$isTv"
    }

    object Player : Screen("player/{tmdbId}/{title}/{season}/{episode}", "Player") {
        fun createMovieRoute(tmdbId: Long, title: String): String =
            "player/$tmdbId/${android.net.Uri.encode(title)}/-1/-1"

        fun createTvRoute(tmdbId: Long, title: String, season: Int, episode: Int): String =
            "player/$tmdbId/${android.net.Uri.encode(title)}/$season/$episode"
    }
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Movies,
    Screen.TvShows,
    Screen.Search,
    Screen.Settings
)

