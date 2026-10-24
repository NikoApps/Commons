package io.github.nikoapps.commons.files

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SafeFilesTest {
    @get:Rule val folder = TemporaryFolder()

    @Test
    fun `uuids are used as they are`() {
        assertEquals("6f1c2a9e-1b2c-4d5e-8f90-123456789abc", safeFileStem("6f1c2a9e-1b2c-4d5e-8f90-123456789abc"))
    }

    @Test
    fun `odd ids can't reach outside the folder`() {
        val stem = safeFileStem("../../shared_prefs/x")
        assertTrue(stem.matches(Regex("id_[0-9a-f]{32}")))
        assertEquals(stem, safeFileStem("../../shared_prefs/x"))
        assertNotEquals(stem, safeFileStem("../../shared_prefs/y"))
        assertTrue(safeFileStem("").startsWith("id_"))
    }

    @Test
    fun `copies into place and leaves no temporary file`() {
        val source = folder.newFile("in.wav").apply { writeText("new") }
        val target = File(folder.root, "kept/out.wav")
        assertTrue(copyIntoPlace(source, target))
        assertEquals("new", target.readText())
        assertFalse(File(target.parentFile, "out.wav.tmp").exists())
    }

    @Test
    fun `a missing source leaves the target as it was`() {
        val target = folder.newFile("out.wav").apply { writeText("old") }
        assertFalse(copyIntoPlace(File(folder.root, "gone.wav"), target))
        assertEquals("old", target.readText())
    }
}
