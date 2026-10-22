# Niko Commons

Shared Android code for [Niko](https://github.com/NikoApps) apps: the theme, speech and the building blocks every app needs. Kotlin and Jetpack Compose, GPLv3. Used by [Niko Clock](https://github.com/NikoApps/Clock).

## What's here

- **`NikoTheme`**: light and dark colour schemes from the Niko colour tokens, and typography that starts larger than Material defaults. Every text colour pairing meets WCAG AAA (7:1), checked by `ContrastTest`. No dynamic colour, so contrast holds on every phone. Each app passes its own `NikoAccent` (`NikoAccent.Clock`, the default, or `NikoAccent.Dialer`); accent pairings are checked by `AccentTest`.
- **`NikoColors`**: the raw tokens, including one background colour per app so people who can't read can tell icons apart.
- **`Speaker`**: a small wrapper around Android's text-to-speech engine. Speaks on the alarm stream by default, because media volume is often turned down. Reports when speech ends, and takes a volume and stream.
- **Components**: `BigButton`, `SecondaryButton`, `SolidButton`, `ScreenTopBar`, and the settings rows `SettingsGroup`, `SwitchRow`, `ValueRow` and `ChoiceRow`. Every one has a spoken label and a touch target of at least 64dp.
- **`PinPad` and `PinDots`**: the number pad for the helper PIN.
- **Helper PIN**: `UnlockPinScreen` and `SetPinScreen`, and `HelperPin`, which hashes the PIN with a salt each app picks once and never changes.
- **Phone health**: `HealthCheck` describes a phone setting that can stop an app working and the page that fixes it. `PhoneHealth` has the checks every app shares (battery optimisation and pausing unused apps), and `PhoneMaker` the maker's own battery settings. Each app adds its own checks.
- **Setup**: `SetupFrame`, the first-run frame with steps, Next, Skip setup and Back. Each app supplies its own steps.
- **Shared settings rows**: voice language, voice problem, speech speed, test voice, helper PIN, phone health and phone maker rows, with `SpeechSpeed`, `TextSize`, `VoicePitch` and `VoiceStatus`. Strings that name the app use its label from the manifest.

Only code with no app-specific logic goes here.

## Using it

Each app adds this repo as a git submodule at `commons/` and includes the library as a Gradle module:

```sh
git submodule add https://github.com/NikoApps/Commons.git commons
```

```kotlin
// settings.gradle.kts
include(":commons")
project(":commons").projectDir = file("commons/commons")

// app/build.gradle.kts
implementation(project(":commons"))
```

The module uses the app's version catalog, so the app's `gradle/libs.versions.toml` must define the same aliases as this repo's: the `android-library` plugin and the libraries in `commons/build.gradle.kts`. CI in each app checks out submodules. To pick up a change in Commons, update the submodule pointer in the app and commit it.

## Building

Requires JDK 17 and the Android SDK.

```sh
./gradlew assembleDebug lint test
```

## Licence

GNU General Public License v3.0. See [LICENSE](LICENSE).
