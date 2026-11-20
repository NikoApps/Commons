package io.github.nikoapps.commons.status

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class PhoneStatusTest {

    @Test
    fun warningsComeMostSeriousFirst() {
        val status = PhoneStatus(airplaneMode = true, doNotDisturb = true, ringer = Ringer.Silent)
        assertEquals(listOf(StatusWarning.AirplaneMode, StatusWarning.DoNotDisturb, StatusWarning.RingerSilent), status.warnings)
    }

    @Test
    fun noSimIsLeftOutInAirplaneMode() {
        assertEquals(listOf(StatusWarning.AirplaneMode), PhoneStatus(airplaneMode = true, simMissing = true).warnings)
        assertEquals(listOf(StatusWarning.NoSim), PhoneStatus(simMissing = true).warnings)
    }

    @Test
    fun ringVolumeOnlyMattersWithTheRingerOn() {
        assertEquals(listOf(StatusWarning.RingVolumeOff), PhoneStatus(ringVolumeZero = true).warnings)
        assertEquals(listOf(StatusWarning.RingerVibrate), PhoneStatus(ringer = Ringer.Vibrate, ringVolumeZero = true).warnings)
        assertTrue(PhoneStatus().warnings.isEmpty())
    }

    @Test
    fun batteryInWords() {
        assertEquals(BatteryLevel.Full, batteryLevelFor(100))
        assertEquals(BatteryLevel.AlmostFull, batteryLevelFor(80))
        assertEquals(BatteryLevel.Half, batteryLevelFor(50))
        assertEquals(BatteryLevel.Low, batteryLevelFor(20))
        assertEquals(BatteryLevel.VeryLow, batteryLevelFor(5))
    }

    @Test
    fun signalBars() {
        assertEquals(listOf(Signal.None, Signal.Weak, Signal.Good, Signal.Strong, Signal.Strong), (0..4).map(::signalFor))
    }

    @Test
    fun englishSentenceSaysWarningsFirst() {
        val status = PhoneStatus(
            airplaneMode = true,
            battery = BatteryLevel.Low,
            charging = true,
            connection = Connection.None,
        )
        assertEquals(
            "Airplane mode is on. Calls and messages can't come in. The battery is low. It is charging. No internet.",
            statusPhrasesFor(Locale.ENGLISH)!!.sentence(status),
        )
    }

    @Test
    fun unknownPartsAreLeftOut() {
        assertEquals("", statusPhrasesFor(Locale.ENGLISH)!!.sentence(PhoneStatus()))
    }

    @Test
    fun everyClockLanguageHasEverySentence() {
        val everything = PhoneStatus(
            airplaneMode = true,
            doNotDisturb = true,
            ringer = Ringer.Vibrate,
            battery = BatteryLevel.VeryLow,
            charging = true,
            signal = Signal.Weak,
            connection = Connection.MobileData,
        )
        listOf("en", "ne-NP", "hi-IN", "es", "pt-BR", "fr-FR", "id-ID", "in-ID", "bn-BD", "ur-PK").forEach { tag ->
            val phrases = statusPhrasesFor(Locale.forLanguageTag(tag))
            assertNotNull(tag, phrases)
            val sentence = phrases!!.sentence(everything)
            assertTrue(tag, sentence.isNotBlank())
            // No digits, so the voice never reads a number in another language.
            assertTrue("$tag: $sentence", sentence.none(Char::isDigit))
            StatusWarning.entries.forEach { assertTrue("$tag $it", phrases.warning(it).isNotBlank()) }
        }
    }

    @Test
    fun noSentencesForOtherLanguages() {
        assertNull(statusPhrasesFor(Locale.GERMAN))
    }
}
