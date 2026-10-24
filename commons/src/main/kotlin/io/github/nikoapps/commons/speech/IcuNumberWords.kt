package io.github.nikoapps.commons.speech

import android.icu.text.MessageFormat
import java.util.Locale

/**
 * Number words from the ICU spellout rules built into Android.
 * Android doesn't expose RuleBasedNumberFormat, but MessageFormat's "spellout" type uses it.
 */
class IcuNumberWords(locale: Locale) : NumberWords {
    private val format = MessageFormat("{0,spellout}", locale)
    private val ordinalFormat = MessageFormat("{0,spellout,%spellout-ordinal}", locale)

    override fun words(number: Int): String = format.format(arrayOf(number))

    override fun ordinal(number: Int): String = ordinalFormat.format(arrayOf(number))
}

/**
 * The phraser for the voice language [languageTag], or the phone's language when it's null.
 * The voice language is separate from the screen language, so it reads the phone's own
 * locale rather than the app's.
 */
fun phraserFor(languageTag: String?): TimePhraser? =
    timePhraserFor(languageTag?.let(Locale::forLanguageTag) ?: phoneLocale()) { IcuNumberWords(it) }

private fun phoneLocale(): Locale = android.content.res.Resources.getSystem().configuration.locales[0]
