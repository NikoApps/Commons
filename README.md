# Niko Commons

Shared Android code for [Niko](https://github.com/NikoApps) apps: the theme, speech and the building blocks every app needs. Kotlin and Jetpack Compose, GPLv3.

## What's here

- **`NikoTheme`**: light and dark colour schemes from the Niko colour tokens, and typography that starts larger than Material defaults. Every text colour pairing meets WCAG AAA (7:1), checked by `ContrastTest`. No dynamic colour, so contrast holds on every phone.
- **`NikoColors`**: the raw tokens, including one background colour per app so people who can't read can tell icons apart.
- **`Speaker`**: a small wrapper around Android's text-to-speech engine. Speaks on the alarm stream by default, because media volume is often turned down. Works in any language the phone's voice engine supports.

## Planned

- **Helper setup screen.** A PIN-locked settings screen for the relative who sets the app up, so the person using it can't change things by accident.
- **Shake detector.** Reliable shake detection with sensible thresholds for older and shaky hands.
- **Reliable scheduling.** Exact alarms and foreground service patterns that keep working through battery savers, reboots and manufacturer task killers (see [dontkillmyapp.com](https://dontkillmyapp.com)).
- **Recorded audio fallback.** Play phrases recorded by a family member when the phone's voice is missing or poor in a language.
- **Accessibility helpers.** Minimum touch target sizes, TalkBack labels and focus order utilities.

## Using it

The library isn't published yet. Apps currently carry a copy of what they need until the first release.

## Building

Requires JDK 17 and the Android SDK.

```sh
./gradlew assembleDebug lint test
```

## Licence

GNU General Public License v3.0. See [LICENSE](LICENSE).
