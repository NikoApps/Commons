package io.github.nikoapps.commons.files

import java.io.File
import java.security.MessageDigest

private val safeId = Regex("[A-Za-z0-9_-]{1,64}")

/**
 * An id as a safe file name without extension, such as for a person's photo or a reminder's
 * recording. Ids are UUIDs, used as they are. Anything else is turned into a hash, so no id can
 * reach outside the folder its files are kept in.
 */
fun safeFileStem(id: String): String {
    if (safeId.matches(id)) return id
    val digest = MessageDigest.getInstance("SHA-256").digest(id.toByteArray(Charsets.UTF_8))
    return "id_" + digest.take(16).joinToString("") { "%02x".format(it) }
}

/**
 * Copies [source] over [target]. Written to a temporary file first and then renamed, so a
 * half-written file is never read. Returns false, leaving [target] as it was, if it couldn't.
 */
fun copyIntoPlace(source: File, target: File): Boolean = runCatching {
    target.parentFile?.mkdirs()
    val temp = File(target.parentFile, target.name + ".tmp")
    source.inputStream().use { input -> temp.outputStream().use { input.copyTo(it) } }
    if (!temp.renameTo(target)) {
        temp.delete()
        return@runCatching false
    }
    true
}.getOrDefault(false)
