package io.github.nikoapps.commons.settings

import java.security.MessageDigest

/**
 * The helper PIN keeps the person using the app from changing settings by accident.
 * It isn't real security (4 digits), so a plain salted hash is enough to keep it out of view.
 * Each app has its own [salt], which goes in front of the PIN. Never change an app's salt once
 * released, or PINs already saved stop matching.
 */
class HelperPin(private val salt: String) {
    fun hash(pin: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$salt$pin".toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun matches(pin: String, hash: String?): Boolean = hash != null && hash(pin) == hash
}
