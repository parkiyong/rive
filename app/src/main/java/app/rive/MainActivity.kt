package app.rive

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import app.rive.ui.navigation.RiveNavGraph
import app.rive.ui.theme.RiveTheme
import app.rive.ui.theme.RiveThemePreset

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Safely enable edge-to-edge (avoid crashing on unsupported Android/TV devices)
        runCatching {
            enableEdgeToEdge()
        }.onFailure { e ->
            Log.w("RiveApp", "enableEdgeToEdge not supported on this device", e)
        }

        val app = (application as? RiveApp) ?: RiveApp.instance

        setContent {
            var currentTheme by remember { mutableStateOf(RiveThemePreset.OBSIDIAN) }

            RiveTheme(preset = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    RiveNavGraph(
                        navController = navController,
                        movieRepository = app.movieRepository,
                        streamRepository = app.streamRepository,
                        historyRepository = app.historyRepository,
                        currentTheme = currentTheme,
                        onThemeChanged = { currentTheme = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

