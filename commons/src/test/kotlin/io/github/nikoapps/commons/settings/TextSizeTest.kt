package io.github.nikoapps.commons.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class TextSizeTest {

    @Test
    fun growsTextOnANormalPhone() {
        assertEquals(1.2f, TextSize.Medium.fontScaleOn(1f), 0.001f)
        assertEquals(1.4f, TextSize.Large.fontScaleOn(1f), 0.001f)
    }

    @Test
    fun stopsAtTwoHundredPercent() {
        assertEquals(2f, TextSize.Large.fontScaleOn(1.5f), 0.001f)
        assertEquals(2f, TextSize.Medium.fontScaleOn(2f), 0.001f)
    }

    @Test
    fun neverShrinksBelowThePhonesOwnSize() {
        assertEquals(2.2f, TextSize.Large.fontScaleOn(2.2f), 0.001f)
        assertEquals(0.9f, TextSize.Small.fontScaleOn(0.9f), 0.001f)
    }
}
