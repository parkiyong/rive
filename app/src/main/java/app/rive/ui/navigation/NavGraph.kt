package app.rive.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import app.rive.data.repository.HistoryRepository
import app.rive.data.repository.MovieRepository
import app.rive.data.repository.StreamRepository
import app.rive.ui.screens.detail.DetailScreen
import app.rive.ui.screens.home.HomeScreen
import app.rive.ui.screens.player.PlayerScreen
import app.rive.ui.screens.search.SearchScreen
import app.rive.ui.screens.settings.SettingsScreen
import app.rive.ui.theme.RiveThemePreset

@Composable
fun RiveNavGraph(
    navController: NavHostController,
    movieRepository: MovieRepository,
    streamRepository: StreamRepository,
    historyRepository: HistoryRepository,
    currentTheme: RiveThemePreset,
    onThemeChanged: (RiveThemePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on fullscreen player screen
    val showBottomBar = currentRoute?.startsWith("player") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    BottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(imageVector = icon, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    movieRepository = movieRepository,
                    historyRepository = historyRepository,
                    category = "home",
                    onMediaClick = { media ->
                        navController.navigate(Screen.Detail.createRoute(media.id, media.isTvShow))
                    },
                    onPlayClick = { media ->
                        if (media.isTvShow) {
                            navController.navigate(Screen.Detail.createRoute(media.id, isTv = true))
                        } else {
                            navController.navigate(Screen.Player.createMovieRoute(media.id, media.displayTitle))
                        }
                    }
                )
            }

            composable(Screen.Movies.route) {
                HomeScreen(
                    movieRepository = movieRepository,
                    historyRepository = historyRepository,
                    category = "movies",
                    onMediaClick = { media ->
                        navController.navigate(Screen.Detail.createRoute(media.id, isTv = false))
                    },
                    onPlayClick = { media ->
                        navController.navigate(Screen.Player.createMovieRoute(media.id, media.displayTitle))
                    }
                )
            }

            composable(Screen.TvShows.route) {
                HomeScreen(
                    movieRepository = movieRepository,
                    historyRepository = historyRepository,
                    category = "tv",
                    onMediaClick = { media ->
                        navController.navigate(Screen.Detail.createRoute(media.id, isTv = true))
                    },
                    onPlayClick = { media ->
                        navController.navigate(Screen.Detail.createRoute(media.id, isTv = true))
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    movieRepository = movieRepository,
                    onMediaClick = { media ->
                        navController.navigate(Screen.Detail.createRoute(media.id, media.isTvShow))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    currentTheme = currentTheme,
                    onThemeSelected = onThemeChanged,
                    onSaveApiKey = { newKey ->
                        movieRepository.updateApiKey(newKey)
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("tmdbId") { type = NavType.LongType },
                    navArgument("isTv") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val tmdbId = backStackEntry.arguments?.getLong("tmdbId") ?: 0L
                val isTv = backStackEntry.arguments?.getBoolean("isTv") ?: false

                DetailScreen(
                    tmdbId = tmdbId,
                    isTv = isTv,
                    movieRepository = movieRepository,
                    historyRepository = historyRepository,
                    onBackClick = { navController.popBackStack() },
                    onPlayMovie = { movieId ->
                        navController.navigate(Screen.Player.createMovieRoute(movieId, "Movie"))
                    },
                    onPlayEpisode = { tvId, season, episode ->
                        navController.navigate(Screen.Player.createTvRoute(tvId, "TV Show", season, episode))
                    },
                    onMediaClick = { media ->
                        navController.navigate(Screen.Detail.createRoute(media.id, media.isTvShow))
                    }
                )
            }

            composable(
                route = Screen.Player.route,
                arguments = listOf(
                    navArgument("tmdbId") { type = NavType.LongType },
                    navArgument("title") { type = NavType.StringType },
                    navArgument("season") { type = NavType.IntType },
                    navArgument("episode") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val tmdbId = backStackEntry.arguments?.getLong("tmdbId") ?: 0L
                val rawTitle = backStackEntry.arguments?.getString("title") ?: "Stream"
                val title = android.net.Uri.decode(rawTitle)
                val season = backStackEntry.arguments?.getInt("season")?.takeIf { it != -1 }
                val episode = backStackEntry.arguments?.getInt("episode")?.takeIf { it != -1 }

                PlayerScreen(
                    tmdbId = tmdbId,
                    title = title,
                    seasonNumber = season,
                    episodeNumber = episode,
                    streamRepository = streamRepository,
                    historyRepository = historyRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

