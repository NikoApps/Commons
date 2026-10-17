package org.nikoapps.commons.theme

import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastTest {

    private val aaa = 7.0

    private fun assertAaa(name: String, fg: Long, bg: Long) {
        val ratio = contrastRatio(fg, bg)
        assertTrue("$name is $ratio:1, needs $aaa:1", ratio >= aaa)
    }

    @Test
    fun lightTextPairsMeetAaa() {
        val bg = 0xFFFFF9EE
        assertAaa("ink on paper", 0xFF1F1A14, bg)
        assertAaa("muted on paper", 0xFF4A4238, bg)
        assertAaa("accent text on paper", 0xFF6E4700, bg)
        assertAaa("success on paper", 0xFF0B5C31, bg)
        assertAaa("danger on paper", 0xFFA3161A, bg)
        assertAaa("info on paper", 0xFF0B4F8A, bg)
        assertAaa("ink on sun", 0xFF1F1A14, 0xFFFFC53D)
    }

    @Test
    fun darkTextPairsMeetAaa() {
        val bg = 0xFF14110D
        assertAaa("paper on night", 0xFFFFF9EE, bg)
        assertAaa("muted on night", 0xFFE9E1D3, bg)
        assertAaa("sun on night", 0xFFFFC53D, bg)
        assertAaa("success on night", 0xFF7BE3A6, bg)
        assertAaa("danger on night", 0xFFFF9A93, bg)
        assertAaa("info on night", 0xFF9CC8FF, bg)
    }

    @Test
    fun inkOnEveryAppColourMeetsAaa() {
        listOf(0xFFFFC53D, 0xFF7BE3A6, 0xFFFF9A93, 0xFFFFE58A, 0xFF9CC8FF, 0xFFC9B8FF, 0xFFFFB067)
            .forEach { assertAaa("ink on app colour ${it.toString(16)}", 0xFF1F1A14, it) }
    }
}
