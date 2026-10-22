package io.github.nikoapps.commons.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccentTest {

    private val aaa = 7.0

    private fun Color.argb(): Long = toArgb().toLong() and 0xFFFFFFFFL

    private fun assertAaa(name: String, fg: Color, bg: Color) {
        val ratio = contrastRatio(fg.argb(), bg.argb())
        assertTrue("$name is $ratio:1, needs $aaa:1", ratio >= aaa)
    }

    private val accents = mapOf(
        "clock" to NikoAccent.Clock,
        "dialer" to NikoAccent.Dialer,
    )

    @Test
    fun lightAccentPairsMeetAaa() {
        accents.forEach { (app, accent) ->
            val c = accent.light
            assertAaa("$app ink on accent", c.onAccent, c.accent)
            assertAaa("$app accent text on background", c.accentText, NikoColors.Light.Background)
            assertAaa("$app accent text on surface", c.accentText, NikoColors.Light.Surface)
            assertAaa("$app paper on accent text", NikoColors.Paper, c.accentText)
        }
    }

    @Test
    fun darkAccentPairsMeetAaa() {
        accents.forEach { (app, accent) ->
            val c = accent.dark
            assertAaa("$app ink on accent", c.onAccent, c.accent)
            assertAaa("$app accent text on background", c.accentText, NikoColors.Dark.Background)
            assertAaa("$app accent text on surface", c.accentText, NikoColors.Dark.Surface)
            assertAaa("$app ink on accent text", NikoColors.Ink, c.accentText)
        }
    }

    @Test
    fun defaultAccentKeepsClockColours() {
        val light = lightScheme(NikoAccent.Clock.light)
        val dark = darkScheme(NikoAccent.Clock.dark)
        assertEquals(Color(0xFFFFC53D), light.primary)
        assertEquals(Color(0xFF1F1A14), light.onPrimary)
        assertEquals(Color(0xFF6E4700), light.secondary)
        assertEquals(Color(0xFFFFC53D), dark.primary)
        assertEquals(Color(0xFF1F1A14), dark.onPrimary)
        assertEquals(Color(0xFFFFC53D), dark.secondary)
    }
}
