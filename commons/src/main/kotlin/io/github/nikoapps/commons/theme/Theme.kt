package io.github.nikoapps.commons.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

internal fun lightScheme(accent: NikoAccentColors): ColorScheme = lightColorScheme(
    primary = accent.accent,
    onPrimary = accent.onAccent,
    secondary = accent.accentText,
    onSecondary = NikoColors.Paper,
    background = NikoColors.Light.Background,
    onBackground = NikoColors.Light.OnBackground,
    surface = NikoColors.Light.Surface,
    onSurface = NikoColors.Light.OnBackground,
    onSurfaceVariant = NikoColors.Light.OnBackgroundMuted,
    outline = NikoColors.Light.Outline,
    error = NikoColors.Light.Danger,
    onError = NikoColors.Paper,
)

internal fun darkScheme(accent: NikoAccentColors): ColorScheme = darkColorScheme(
    primary = accent.accent,
    onPrimary = accent.onAccent,
    secondary = accent.accentText,
    onSecondary = NikoColors.Ink,
    background = NikoColors.Dark.Background,
    onBackground = NikoColors.Dark.OnBackground,
    surface = NikoColors.Dark.Surface,
    onSurface = NikoColors.Dark.OnBackground,
    onSurfaceVariant = NikoColors.Dark.OnBackgroundMuted,
    outline = NikoColors.Dark.Outline,
    error = NikoColors.Dark.Danger,
    onError = NikoColors.Ink,
)

/**
 * The shared Niko theme. No dynamic colour: wallpaper-based palettes can break
 * contrast and make apps look different on every phone.
 *
 * [accent] is the app's own colour. The default is Niko Clock's sun yellow.
 */
@Composable
fun NikoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: NikoAccent = NikoAccent.Clock,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(darkTheme, accent) {
        if (darkTheme) darkScheme(accent.dark) else lightScheme(accent.light)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NikoTypography,
        content = content,
    )
}
