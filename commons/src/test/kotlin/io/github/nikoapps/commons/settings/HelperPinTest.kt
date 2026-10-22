package io.github.nikoapps.commons.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HelperPinTest {
    private val pin = HelperPin("test-salt:")

    @Test
    fun matchesItsOwnHash() {
        assertTrue(pin.matches("1234", pin.hash("1234")))
        assertFalse(pin.matches("1235", pin.hash("1234")))
    }

    @Test
    fun noSavedPinNeverMatches() {
        assertFalse(pin.matches("1234", null))
    }

    @Test
    fun hashIsSha256OfSaltThenPin() {
        // printf 'test-salt:1234' | sha256sum
        assertEquals("dd565b46289eb4a5a2f160be63a156ef529913865d6648643f4cadaeee1f52ba", pin.hash("1234"))
    }

    @Test
    fun saltChangesTheHash() {
        assertNotEquals(pin.hash("1234"), HelperPin("other-salt:").hash("1234"))
    }
}
