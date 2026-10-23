package io.github.nikoapps.commons.settings

/** Every setting is a choice of three plain options, never a number to type. */
enum class SpeechSpeed(val rate: Float) {
    Slow(0.75f),
    Normal(0.9f),
    Fast(1.1f),
    ;

    companion object {
        /** The option closest to a stored speech rate, where 1.0 is the engine's normal speed. */
        fun closestTo(rate: Float): SpeechSpeed = entries.minBy { kotlin.math.abs(it.rate - rate) }
    }
}

/** On top of the phone's own font size. */
enum class TextSize(val scale: Float) {
    Small(1.0f),
    Medium(1.2f),
    Large(1.4f),
    ;

    /**
     * The font scale to draw with on a phone set to [phoneScale]. The app's choice makes text bigger
     * only up to 200%, or the phone's own size if that is bigger already, because past that the main
     * screen can no longer fit what matters.
     */
    fun fontScaleOn(phoneScale: Float): Float =
        (phoneScale * scale).coerceAtMost(maxOf(phoneScale, MAX_APP_FONT_SCALE))

    private companion object {
        const val MAX_APP_FONT_SCALE = 2f
    }
}

enum class VoicePitch(val pitch: Float) { Lower(0.85f), Normal(1.0f), Higher(1.15f) }
