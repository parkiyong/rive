package app.rive.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class RiveThemePreset(val displayName: String) {
    OBSIDIAN("Obsidian Luxury"),
    MIDNIGHT("Midnight Blue"),
    AURORA("Aurora Glow")
}

private val ObsidianColorScheme = darkColorScheme(
    primary = ObsidianPrimary,
    secondary = ObsidianSecondary,
    background = ObsidianBackground,
    surface = ObsidianSurface,
    surfaceVariant = ObsidianSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val MidnightColorScheme = darkColorScheme(
    primary = MidnightPrimary,
    secondary = MidnightSecondary,
    background = MidnightBackground,
    surface = MidnightSurface,
    surfaceVariant = MidnightSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val AuroraColorScheme = darkColorScheme(
    primary = AuroraPrimary,
    secondary = AuroraSecondary,
    background = AuroraBackground,
    surface = AuroraSurface,
    surfaceVariant = AuroraSurfaceVariant,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

@Composable
fun RiveTheme(
    preset: RiveThemePreset = RiveThemePreset.OBSIDIAN,
    content: @Composable () -> Unit
) {
    val colorScheme = when (preset) {
        RiveThemePreset.OBSIDIAN -> ObsidianColorScheme
        RiveThemePreset.MIDNIGHT -> MidnightColorScheme
        RiveThemePreset.AURORA -> AuroraColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

