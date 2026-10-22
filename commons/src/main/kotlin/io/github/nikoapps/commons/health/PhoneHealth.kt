package io.github.nikoapps.commons.health

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import io.github.nikoapps.commons.R

/**
 * A phone setting that can stop an app doing its job, such as chimes or silencing calls.
 * Each app lists the checks that matter for what's turned on; [PhoneHealth] has the ones every
 * app shares. Checks are compared by identity, so keep each one in a `val`.
 *
 * @param title the setting's name, such as "Battery use".
 * @param fix what to change. It may use %1$s for the app's name.
 * @param isProblem true when the setting is wrong on this phone.
 * @param fixIntent the page that fixes it. Callers fall back to app info if it won't open.
 */
class HealthCheck(
    @StringRes val title: Int,
    @StringRes val fix: Int,
    val isProblem: (Context) -> Boolean,
    val fixIntent: (Context) -> Intent,
)

/** The checks every Niko app shares, and the pages that fix them. */
object PhoneHealth {

    /** Battery optimisation is on, so the phone may stop the app in the background. */
    val Battery = HealthCheck(
        title = R.string.health_battery,
        fix = R.string.health_battery_value,
        isProblem = { context ->
            context.getSystemService<PowerManager>()?.isIgnoringBatteryOptimizations(context.packageName) == false
        },
        // App info has "Battery" or "App battery usage" one tap away on most phones. Asking
        // directly would need another permission.
        fixIntent = ::appInfo,
    )

    /** "Pause app activity if unused" is on. A person who never opens the app would lose it after a few months. */
    val Hibernation = HealthCheck(
        title = R.string.health_hibernation,
        fix = R.string.health_hibernation_value,
        isProblem = { context ->
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !context.packageManager.isAutoRevokeWhitelisted
        },
        fixIntent = { context -> Intent(Intent.ACTION_AUTO_REVOKE_PERMISSIONS, packageUri(context)) },
    )

    /** The checks in [checks] that are wrong on this phone, in the same order. */
    fun problems(context: Context, checks: List<HealthCheck>): List<HealthCheck> =
        checks.filter { it.isProblem(context) }

    fun appInfo(context: Context): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri(context))

    /** "package:<this app>", the address most per-app settings pages take. */
    fun packageUri(context: Context) = "package:${context.packageName}".toUri()
}

/** Phone makers with their own battery killers on top of Android's. */
enum class PhoneMaker {
    Xiaomi,
    Samsung,
    Other,
    ;

    /** The maker's own page where it has one, otherwise app info. Callers fall back to app info if it won't open. */
    fun settingsIntent(context: Context): Intent = when (this) {
        Xiaomi -> Intent().setComponent(
            ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
        )
        Samsung, Other -> PhoneHealth.appInfo(context)
    }

    companion object {
        fun current(): PhoneMaker = when (Build.MANUFACTURER.lowercase()) {
            "xiaomi", "redmi", "poco" -> Xiaomi
            "samsung" -> Samsung
            else -> Other
        }
    }
}
