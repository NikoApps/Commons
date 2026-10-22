package io.github.nikoapps.commons.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Read out by TalkBack whenever it changes, such as after a wrong PIN.
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
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
