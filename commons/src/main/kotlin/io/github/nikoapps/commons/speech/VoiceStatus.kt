package io.github.nikoapps.commons.speech

/** Whether the app can speak right now, and if not, why. */
enum class VoiceStatus {
    /** The speech engine is still starting. */
    Starting,
    Ready,

    /** The phone has no text-to-speech engine at all. */
    NoEngine,

    /** There's an engine, but no installed offline voice for the language. */
    NoVoice,

    /** The app has no phrases for the language yet. */
    LanguageNotSupported,
}
