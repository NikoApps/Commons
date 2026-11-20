package io.github.nikoapps.commons.status

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

/** Battery charge in words. Words, not a percentage, so there is no number to follow. */
enum class BatteryLevel { Full, AlmostFull, Half, Low, VeryLow }

/** Mobile signal from Android's own 0 to 4 bars. */
enum class Signal { None, Weak, Good, Strong }

/** How the phone reaches the internet right now. */
enum class Connection { WiFi, MobileData, None }

enum class Ringer { On, Vibrate, Silent }

/** Something that stops calls or messages reaching the person, most serious first. */
enum class StatusWarning { AirplaneMode, NoSim, DoNotDisturb, RingerSilent, RingerVibrate, RingVolumeOff }

/**
 * What a "what's wrong with my phone" check reports. Null fields are unknown on this phone, such
 * as signal on a tablet or before Android 9, and are left out of what's said.
 */
data class PhoneStatus(
    val airplaneMode: Boolean = false,
    val doNotDisturb: Boolean = false,
    val ringer: Ringer = Ringer.On,
    /** The ringer is on, but its volume is at zero. */
    val ringVolumeZero: Boolean = false,
    val simMissing: Boolean = false,
    val battery: BatteryLevel? = null,
    val charging: Boolean = false,
    val signal: Signal? = null,
    val connection: Connection? = null,
) {
    val warnings: List<StatusWarning>
        get() = buildList {
            if (airplaneMode) add(StatusWarning.AirplaneMode)
            // With airplane mode on, the SIM can look missing; one warning is enough.
            if (simMissing && !airplaneMode) add(StatusWarning.NoSim)
            if (doNotDisturb) add(StatusWarning.DoNotDisturb)
            when (ringer) {
                Ringer.Silent -> add(StatusWarning.RingerSilent)
                Ringer.Vibrate -> add(StatusWarning.RingerVibrate)
                Ringer.On -> if (ringVolumeZero) add(StatusWarning.RingVolumeOff)
            }
        }
}

fun batteryLevelFor(percent: Int): BatteryLevel = when {
    percent >= 95 -> BatteryLevel.Full
    percent >= 70 -> BatteryLevel.AlmostFull
    percent >= 35 -> BatteryLevel.Half
    percent >= 15 -> BatteryLevel.Low
    else -> BatteryLevel.VeryLow
}

/** [bars] is Android's signal level, 0 to 4. */
fun signalFor(bars: Int): Signal = when {
    bars <= 0 -> Signal.None
    bars == 1 -> Signal.Weak
    bars == 2 -> Signal.Good
    else -> Signal.Strong
}

/**
 * Reads the phone's status. Needs no runtime permission. The connection needs
 * `ACCESS_NETWORK_STATE` in the app's manifest (granted at install); without it the connection is
 * left out. Never needs `READ_PHONE_STATE`: "no service" isn't reported, only signal bars and a
 * missing SIM.
 */
object PhoneStatusReader {

    fun read(context: Context): PhoneStatus {
        val base = warningsOnly(context)
        val battery = context.getSystemService(BatteryManager::class.java)
        val percent = battery?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.takeIf { it in 0..100 }
        val telephony = telephony(context)
        val signal = if (telephony == null || base.airplaneMode || base.simMissing || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            null
        } else {
            runCatching { telephony.signalStrength?.level }.getOrNull()?.let(::signalFor)
        }
        return base.copy(
            battery = percent?.let(::batteryLevelFor),
            charging = battery?.isCharging == true,
            signal = signal,
            connection = runCatching { connection(context) }.getOrNull(),
        )
    }

    /** Only what goes into [PhoneStatus.warnings]: cheap enough to check every few seconds. */
    fun warningsOnly(context: Context): PhoneStatus {
        val airplane = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1
        val filter = context.getSystemService(NotificationManager::class.java)?.currentInterruptionFilter
            ?: NotificationManager.INTERRUPTION_FILTER_UNKNOWN
        val dnd = filter != NotificationManager.INTERRUPTION_FILTER_ALL && filter != NotificationManager.INTERRUPTION_FILTER_UNKNOWN
        val audio = context.getSystemService(AudioManager::class.java)
        val ringer = when (audio?.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> Ringer.Silent
            AudioManager.RINGER_MODE_VIBRATE -> Ringer.Vibrate
            else -> Ringer.On
        }
        val ringZero = ringer == Ringer.On && audio?.getStreamVolume(AudioManager.STREAM_RING) == 0
        val simMissing = telephony(context)?.simState == TelephonyManager.SIM_STATE_ABSENT
        return PhoneStatus(
            airplaneMode = airplane,
            doNotDisturb = dnd,
            ringer = ringer,
            ringVolumeZero = ringZero,
            simMissing = simMissing,
        )
    }

    /** Null on phones that can't make calls, such as most tablets. */
    private fun telephony(context: Context): TelephonyManager? =
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            context.getSystemService(TelephonyManager::class.java)
        } else {
            null
        }

    /** Null when the app doesn't declare `ACCESS_NETWORK_STATE`. Lint misses the check on the first line. */
    @SuppressLint("MissingPermission")
    private fun connection(context: Context): Connection? {
        val allowed = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
        if (!allowed) return null
        val manager = context.getSystemService(ConnectivityManager::class.java) ?: return Connection.None
        val caps = manager.getNetworkCapabilities(manager.activeNetwork) ?: return Connection.None
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Connection.WiFi
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Connection.MobileData
            else -> Connection.None
        }
    }
}
