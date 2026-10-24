package io.github.nikoapps.commons.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** The accent colours for one theme: the accent surface, ink on it, and accent-coloured text. */
@Immutable
data class NikoAccentColors(
    val accent: Color,
    val onAccent: Color,
    val accentText: Color,
)

/**
 * Each app's own accent, taken from its icon gradient, so the app looks like its icon.
 * Every pairing meets WCAG AAA (7:1) in light and dark. See AccentTest.
 */
@Immutable
data class NikoAccent(
    val light: NikoAccentColors,
    val dark: NikoAccentColors,
) {
    companion object {
        /** Sun yellow. The default, and what Niko Clock has always used. */
        val Clock = NikoAccent(
            light = NikoAccentColors(
                accent = NikoColors.Light.Accent,
                onAccent = NikoColors.Light.OnAccent,
                accentText = NikoColors.Light.AccentText,
            ),
            dark = NikoAccentColors(
                accent = NikoColors.Dark.Accent,
                onAccent = NikoColors.Dark.OnAccent,
                accentText = NikoColors.Dark.AccentText,
            ),
        )

        /** Green, from the darker stop of the Dialer icon gradient (#B5F2CC to #7BE3A6). */
        val Dialer = NikoAccent(
            light = NikoAccentColors(
                accent = NikoColors.Apps.Dialer,
                onAccent = NikoColors.Ink,
                accentText = Color(0xFF185C38),
            ),
            dark = NikoAccentColors(
                accent = NikoColors.Apps.Dialer,
                onAccent = NikoColors.Ink,
                accentText = NikoColors.Apps.Dialer,
            ),
        )

        /** Coral, from the darker stop of the Reminders icon gradient (#FFC7C2 to #FF9A93). */
        val Reminders = NikoAccent(
            light = NikoAccentColors(
                accent = NikoColors.Apps.Reminders,
                onAccent = NikoColors.Ink,
                accentText = Color(0xFF8A2A22),
            ),
            dark = NikoAccentColors(
                accent = NikoColors.Apps.Reminders,
                onAccent = NikoColors.Ink,
                accentText = NikoColors.Apps.Reminders,
            ),
        )
    }
}
