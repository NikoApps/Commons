package io.github.nikoapps.commons.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings as AndroidSettings
import android.speech.tts.TextToSpeech
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.nikoapps.commons.R
import io.github.nikoapps.commons.health.HealthCheck
import io.github.nikoapps.commons.health.PhoneHealth
import io.github.nikoapps.commons.health.PhoneMaker
import io.github.nikoapps.commons.settings.SpeechSpeed
import io.github.nikoapps.commons.settings.TextSize
import io.github.nikoapps.commons.settings.VoicePitch
import io.github.nikoapps.commons.speech.VoiceStatus

// Rows every Niko app's settings and first-run setup share, so they always look and behave the same.

/** Each language in its own words, so people can find theirs without reading another. */
private val LANGUAGE_NAMES = mapOf(
    "en" to "English",
    "ne" to "नेपाली",
    "hi" to "हिन्दी",
    "bn" to "বাংলা",
    "ur" to "اردو",
    "ar" to "العربية",
    "es" to "Español",
    "pt" to "Português",
    "fr" to "Français",
    // Android still calls Indonesian "in"; newer code says "id".
    "in" to "Bahasa Indonesia",
    "id" to "Bahasa Indonesia",
)

/** Opens another app's page, or the phone's settings if this phone doesn't have that page. */
fun openPage(context: Context, intent: Intent, fallback: Intent = Intent(AndroidSettings.ACTION_SETTINGS)) {
    try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        context.startActivity(fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: SecurityException) {
        context.startActivity(fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

/** Gives rows below it their (i) buttons. */
@Composable
fun ProvideInfoStrings(content: @Composable () -> Unit) {
    val resources = LocalResources.current
    val infoStrings = InfoStrings(
        icon = painterResource(R.drawable.ic_info),
        moreAbout = { title -> resources.getString(R.string.info_more_about, title) },
        ok = stringResource(R.string.ok),
    )
    CompositionLocalProvider(LocalInfoStrings provides infoStrings, content = content)
}

/** The app's name as the phone shows it, such as "Niko Clock", for strings that name the app. */
@Composable
fun appName(): String {
    val context = LocalContext.current
    return context.applicationInfo.loadLabel(context.packageManager).toString()
}

/** A language tag such as "ne-NP" in its own words, or "Same as phone" for null. */
@Composable
fun languageName(tag: String?): String =
    tag?.let { LANGUAGE_NAMES[it.substringBefore('-')] } ?: stringResource(R.string.language_phone)

/** [languages] are the ones the app has hand-written phrases for, with null for "Same as phone". */
@Composable
fun VoiceLanguageRow(languages: List<String?>, selected: String?, onSelect: (String?) -> Unit) {
    ChoiceRow(
        title = stringResource(R.string.setting_voice_language),
        options = languages,
        selected = languages.firstOrNull { it == selected },
        optionLabel = { languageName(it) },
        onSelect = onSelect,
        cancelLabel = stringResource(R.string.cancel),
    )
}

/** Only shown when something is wrong, and it says how to fix it. */
@Composable
fun VoiceProblemRow(voice: VoiceStatus) {
    val message = voiceMessage(voice) ?: return
    val context = LocalContext.current
    ValueRow(
        title = stringResource(R.string.voice_problem),
        value = message,
        valueIsWarning = true,
        trailing = painterResource(R.drawable.ic_chevron_right),
        onClick = { openPage(context, Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)) },
    )
}

@Composable
fun SpeechSpeedRow(speed: SpeechSpeed, onSelect: (SpeechSpeed) -> Unit) {
    ChoiceRow(
        title = stringResource(R.string.setting_speech_speed),
        options = SpeechSpeed.entries,
        selected = speed,
        optionLabel = { stringResource(it.label()) },
        onSelect = onSelect,
        cancelLabel = stringResource(R.string.cancel),
    )
}

/** "Test the voice", with the speech engine's name under it so a helper knows which voice is in use. */
@Composable
fun TestVoiceRow(engine: String?, onTestVoice: () -> Unit) {
    ValueRow(
        title = stringResource(R.string.test_voice),
        value = engine?.let { stringResource(R.string.voice_engine, it) },
        onClick = onTestVoice,
    )
}

/** The helper PIN row, and Remove the PIN while one is set. [info] says what the PIN guards in this app. */
@Composable
fun PinRows(hasPin: Boolean, info: String, onSetPin: () -> Unit, onRemovePin: () -> Unit) {
    ValueRow(
        title = stringResource(R.string.section_pin),
        info = info,
        value = stringResource(if (hasPin) R.string.value_on else R.string.value_off),
        trailing = painterResource(R.drawable.ic_chevron_right),
        onClick = onSetPin,
    )
    if (hasPin) {
        ValueRow(title = stringResource(R.string.pin_remove), value = null, onClick = onRemovePin)
    }
}

/**
 * One row per phone setting that can stop the app working, each opening the page that fixes it.
 * [onFix] replaces opening [HealthCheck.fixIntent], for checks such as a permission that the app
 * asks for itself.
 */
@Composable
fun HealthRows(problems: List<HealthCheck>, onFix: ((HealthCheck) -> Unit)? = null) {
    val context = LocalContext.current
    val chevron = painterResource(R.drawable.ic_chevron_right)
    val name = appName()
    problems.forEach { problem ->
        ValueRow(
            title = stringResource(problem.title),
            value = stringResource(problem.fix, name),
            valueIsWarning = true,
            trailing = chevron,
            onClick = {
                if (onFix != null) {
                    onFix(problem)
                } else {
                    openPage(context, problem.fixIntent(context), PhoneHealth.appInfo(context))
                }
            },
        )
    }
}

/**
 * The phone maker's own battery and autostart settings. Android can't tell us whether these are
 * set, so this row is always shown, with steps for the maker of this phone. [info] says what the
 * phone's battery saver can stop in this app.
 */
@Composable
fun PhoneMakerRow(info: String) {
    val context = LocalContext.current
    val maker = PhoneMaker.current()
    ValueRow(
        title = stringResource(R.string.phone_maker),
        info = info,
        value = stringResource(
            when (maker) {
                PhoneMaker.Xiaomi -> R.string.phone_maker_xiaomi
                PhoneMaker.Samsung -> R.string.phone_maker_samsung
                PhoneMaker.Other -> R.string.phone_maker_other
            },
            appName(),
        ),
        trailing = painterResource(R.drawable.ic_chevron_right),
        onClick = { openPage(context, maker.settingsIntent(context), PhoneHealth.appInfo(context)) },
    )
}

@StringRes
fun SpeechSpeed.label(): Int = when (this) {
    SpeechSpeed.Slow -> R.string.speed_slow
    SpeechSpeed.Normal -> R.string.speed_normal
    SpeechSpeed.Fast -> R.string.speed_fast
}

@StringRes
fun TextSize.label(): Int = when (this) {
    TextSize.Small -> R.string.size_small
    TextSize.Medium -> R.string.size_medium
    TextSize.Large -> R.string.size_large
}

@StringRes
fun VoicePitch.label(): Int = when (this) {
    VoicePitch.Lower -> R.string.pitch_lower
    VoicePitch.Normal -> R.string.pitch_normal
    VoicePitch.Higher -> R.string.pitch_higher
}

/** What's wrong with the voice and how to fix it, or null while it's starting or ready. */
@Composable
fun voiceMessage(voice: VoiceStatus): String? = when (voice) {
    VoiceStatus.Starting, VoiceStatus.Ready -> null
    VoiceStatus.NoVoice -> stringResource(R.string.status_no_voice)
    VoiceStatus.NoEngine -> stringResource(R.string.status_no_engine)
    VoiceStatus.LanguageNotSupported -> stringResource(R.string.status_language_not_supported, appName())
}
