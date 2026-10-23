package io.github.nikoapps.commons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.nikoapps.commons.R
import io.github.nikoapps.commons.settings.HelperPin

/** Asks for the helper PIN before settings open. [pinHash] is the saved hash from [helperPin]. */
@Composable
fun UnlockPinScreen(helperPin: HelperPin, pinHash: String, onUnlocked: () -> Unit, onCancel: () -> Unit) {
    // Plain remember, not rememberSaveable: PIN digits must never go into the saved instance state.
    var entered by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf(false) }
    PinLayout(
        title = stringResource(R.string.pin_enter_title),
        message = if (wrong) stringResource(R.string.pin_wrong) else stringResource(R.string.pin_enter_message),
        entered = entered.length,
        onDigit = { digit ->
            if (entered.length < PIN_LENGTH) entered += digit
            if (entered.length == PIN_LENGTH) {
                if (helperPin.matches(entered, pinHash)) onUnlocked() else wrong = true
                entered = ""
            }
        },
        onDelete = { entered = entered.dropLast(1) },
        onCancel = onCancel,
    )
}

/** Sets a new PIN: enter it, then enter it again to confirm. [onPinSet] gets the digits, for the app to hash. */
@Composable
fun SetPinScreen(onPinSet: (String) -> Unit, onCancel: () -> Unit) {
    // Plain remember, not rememberSaveable: PIN digits must never go into the saved instance state.
    var first by remember { mutableStateOf("") }
    var entered by remember { mutableStateOf("") }
    var mismatch by remember { mutableStateOf(false) }
    val confirming = first.isNotEmpty()
    PinLayout(
        title = stringResource(R.string.pin_set_title),
        message = when {
            mismatch -> stringResource(R.string.pin_mismatch)
            confirming -> stringResource(R.string.pin_confirm_message)
            else -> stringResource(R.string.pin_set_message)
        },
        entered = entered.length,
        onDigit = { digit ->
            if (entered.length < PIN_LENGTH) entered += digit
            if (entered.length == PIN_LENGTH) {
                when {
                    !confirming -> { first = entered; mismatch = false }
                    entered == first -> onPinSet(entered)
                    else -> { first = ""; mismatch = true }
                }
                entered = ""
            }
        },
        onDelete = { entered = entered.dropLast(1) },
        onCancel = onCancel,
    )
}

@Composable
private fun PinLayout(
    title: String,
    message: String,
    entered: Int,
    onDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        ScreenTopBar(
            title = title,
            backIcon = painterResource(R.drawable.ic_arrow_back),
            backLabel = stringResource(R.string.back),
            onBack = onCancel,
        )
        // The message shrinks to fit the room left above the keys. The dots and keys stay on screen
        // at any font size, so a PIN with 0 in it never needs a scroll first.
        val messageStyle = MaterialTheme.typography.bodyLarge
        BasicText(
            text = message,
            // Line height in em, so shrunk lines don't keep the full-size gaps.
            style = messageStyle.copy(color = MaterialTheme.colorScheme.onBackground, lineHeight = 1.3.em),
            autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = messageStyle.fontSize),
            // Read out by TalkBack whenever it changes, such as after a wrong PIN.
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
        // The digits are big already; past 130% they would push the bottom row off the screen.
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalDensity provides Density(density.density, density.fontScale.coerceAtMost(MAX_PAD_FONT_SCALE)),
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                PinDots(entered = entered, description = stringResource(R.string.pin_progress, entered, PIN_LENGTH))
                PinPad(
                    onDigit = onDigit,
                    onDelete = onDelete,
                    deleteIcon = painterResource(R.drawable.ic_backspace),
                    deleteLabel = stringResource(R.string.pin_delete),
                )
            }
        }
    }
}

private const val MAX_PAD_FONT_SCALE = 1.3f
