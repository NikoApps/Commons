package io.github.nikoapps.commons.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.LayoutDirection
import io.github.nikoapps.commons.R

/** One step of first-run setup: a group title and the settings rows under it. */
class SetupStep(val title: String, val content: @Composable () -> Unit)

/**
 * First-run setup for the helper, one short step at a time, using the same rows as settings.
 * Each app supplies its own [steps]. Nothing is required: Next moves on whatever is chosen, and
 * Skip leaves at any point. Next and Skip sit side by side in calm colours, or stacked when a large
 * font would break a label; the big accent button belongs to the main screen. [step] is kept by the
 * caller so it survives a trip to the PIN screen.
 */
@Composable
fun SetupFrame(steps: List<SetupStep>, step: Int, onStep: (Int) -> Unit, onFinish: () -> Unit) {
    val index = step.coerceIn(0, steps.lastIndex)
    val current = steps[index]
    val last = index == steps.lastIndex
    val back = { if (step > 0) onStep(step - 1) else onFinish() }
    BackHandler(onBack = back)

    ProvideInfoStrings {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            ScreenTopBar(
                title = stringResource(R.string.setup_title),
                backIcon = painterResource(R.drawable.ic_arrow_back),
                backLabel = stringResource(R.string.back),
                onBack = back,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                // The rows explain themselves through their (i) buttons, so the step needs no intro text.
                Text(
                    text = stringResource(R.string.setup_step, index + 1, steps.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Read out when Next or Back changes the step, since the title stays the same.
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
                SettingsGroup(current.title) {
                    current.content()
                }
            }
            SetupButtons(
                skip = if (last) null else stringResource(R.string.setup_skip),
                next = stringResource(if (last) R.string.setup_done else R.string.setup_next),
                onSkip = onFinish,
                onNext = { if (last) onFinish() else onStep(step + 1) },
            )
        }
    }
}

/**
 * Skip and Next side by side at the same height while both labels fit on one line. When a large
 * font would break a word across lines, they stack full width with Next on top.
 */
@Composable
private fun SetupButtons(skip: String?, next: String, onSkip: () -> Unit, onNext: () -> Unit) {
    val measurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.labelLarge
    val density = LocalDensity.current
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        // Half the row less the gap, less the button's own side padding.
        val labelRoom = with(density) {
            ((maxWidth - ButtonGap) / 2 - ButtonDefaults.ContentPadding.calculateLeftPadding(LayoutDirection.Ltr) * 2)
                .roundToPx()
        }
        val fits = listOfNotNull(skip, next).all { label ->
            measurer.measure(label, style, maxLines = 1, softWrap = false).size.width <= labelRoom
        }
        when {
            skip == null -> SolidButton(text = next, onClick = onNext, modifier = Modifier.fillMaxWidth())
            fits -> Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(ButtonGap),
            ) {
                SecondaryButton(text = skip, onClick = onSkip, modifier = Modifier.weight(1f).fillMaxHeight())
                SolidButton(text = next, onClick = onNext, modifier = Modifier.weight(1f).fillMaxHeight())
            }
            else -> Column(verticalArrangement = Arrangement.spacedBy(ButtonGap)) {
                SolidButton(text = next, onClick = onNext, modifier = Modifier.fillMaxWidth())
                SecondaryButton(text = skip, onClick = onSkip)
            }
        }
    }
}

private val ButtonGap = 12.dp
