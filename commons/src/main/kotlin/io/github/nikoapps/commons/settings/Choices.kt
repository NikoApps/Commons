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
enum class TextSize(val scale: Float) { Small(1.0f), Medium(1.2f), Large(1.4f) }

enum class VoicePitch(val pitch: Float) { Lower(0.85f), Normal(1.0f), Higher(1.15f) }
