package com.device.mask

import android.app.Application
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.net.wifi.WifiInfo
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.euicc.EuiccManager
import android.webkit.WebSettings
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam
import java.net.NetworkInterface
import java.util.Locale
import java.util.TimeZone

/**
 * Device Mask — display-only per-app device identity spoofer.
 *
 * Fresh implementation. The module makes the apps it is enabled for see
 * a different device: model, manufacturer, brand, fingerprint, codename,
 * country, GPS, timezone, operator, MCC/MNC, SIM serial, IMSI, phone
 * number, EID, eSIM support, IMEI, MAC address and WebView user-agent —
 * each behind its own on/off switch in the app. The real device, SIM and
 * eSIM are never changed: every value shown to a target app is display
 * only.
 *
 * Settings are read through the framework's RemotePreferences
 * (XSharedPreferences does not exist in the libxposed API): the module
 * app's XposedProvider serves its own SharedPreferences file to the
 * framework, so no world-readable file is needed.
 *
 * Every hook is registered defensively: the method is looked up first and
 * the registration is wrapped in try/catch, so on an Android version
 * where a method does not exist the module simply does nothing instead
 * of crashing the target app. Build.* fields are set by reflection,
 * which is the only way to spoof them (they are fields, not methods).
 */
class ModuleMain : XposedModule() {

    /** One snapshot of the user's settings, read fresh on every hook call. */
    private data class Settings(
        val deviceOn: Boolean,
        val locationOn: Boolean,
        val networkOn: Boolean,
        val eidOn: Boolean,
        val esimOn: Boolean,
        val imeiOn: Boolean,
        val macOn: Boolean,
        val userAgentOn: Boolean,
        val model: DeviceModel,
        val country: Country,
        val imei: String,
        val mac: String,
        val eid: String,
        val userAgent: String,
        val iccid: String,
        val imsi: String,
        val phone: String
    )

    /**
     * Reads the settings the user chose in the Device Mask app.
     * Runs on every hook call so a toggle change takes effect without a
     * reboot (after the first reboot that loads the module).
     * If the preferences cannot be read at all, everything falls back
     * to the defaults — device/location/network/eSIM on, privacy IDs off.
     */
    private fun readSettings(): Settings {
        return try {
            val prefs = getRemotePreferences(Prefs.FILE_NAME)
            val modelIndex = prefs.getInt(
                Prefs.KEY_SELECTED_MODEL, Prefs.DEFAULT_SELECTED_MODEL
            ).coerceIn(DEVICE_MODELS.indices)
            val countryIndex = prefs.getInt(
                Prefs.KEY_SELECTED_COUNTRY, Prefs.DEFAULT_SELECTED_COUNTRY
            ).coerceIn(COUNTRIES.indices)
            val model = DEVICE_MODELS[modelIndex]
            val country = COUNTRIES[countryIndex]
            Settings(
                deviceOn = prefs.getBoolean(
                    Prefs.KEY_DEVICE_ENABLED, Prefs.DEFAULT_DEVICE_ENABLED
                ),
                locationOn = prefs.getBoolean(
                    Prefs.KEY_LOCATION_ENABLED, Prefs.DEFAULT_LOCATION_ENABLED
                ),
                networkOn = prefs.getBoolean(
                    Prefs.KEY_NETWORK_ENABLED, Prefs.DEFAULT_NETWORK_ENABLED
                ),
                eidOn = prefs.getBoolean(
                    Prefs.KEY_EID_ENABLED, Prefs.DEFAULT_EID_ENABLED
                ),
                esimOn = prefs.getBoolean(
                    Prefs.KEY_ESIM_ENABLED, Prefs.DEFAULT_ESIM_ENABLED
                ),
                imeiOn = prefs.getBoolean(
                    Prefs.KEY_IMEI_ENABLED, Prefs.DEFAULT_IMEI_ENABLED
                ),
                macOn = prefs.getBoolean(
                    Prefs.KEY_MAC_ENABLED, Prefs.DEFAULT_MAC_ENABLED
                ),
                userAgentOn = prefs.getBoolean(
                    Prefs.KEY_USER_AGENT_ENABLED, Prefs.DEFAULT_USER_AGENT_ENABLED
                ),
                model = model,
                country = country,
                imei = prefs.getString(Prefs.KEY_IMEI, "") ?: "",
                mac = prefs.getString(Prefs.KEY_MAC, "") ?: "",
                eid = prefs.getString(Prefs.KEY_EID, "") ?: "",
                userAgent = prefs.getString(Prefs.KEY_USER_AGENT, "")
                    ?: defaultUserAgent(model),
                iccid = prefs.getString(Prefs.KEY_ICCID, "") ?: "",
                imsi = prefs.getString(Prefs.KEY_IMSI, "") ?: "",
                phone = prefs.getString(Prefs.KEY_PHONE, "") ?: ""
            )
        } catch (e: Throwable) {
            // Preferences unreadable: safe defaults, never crash.
            val model = DEVICE_MODELS[Prefs.DEFAULT_SELECTED_MODEL]
            val country = COUNTRIES[Prefs.DEFAULT_SELECTED_COUNTRY]
            Settings(
                deviceOn = Prefs.DEFAULT_DEVICE_ENABLED,
                locationOn = Prefs.DEFAULT_LOCATION_ENABLED,
                networkOn = Prefs.DEFAULT_NETWORK_ENABLED,
                eidOn = Prefs.DEFAULT_EID_ENABLED,
                esimOn = Prefs.DEFAULT_ESIM_ENABLED,
                imeiOn = Prefs.DEFAULT_IMEI_ENABLED,
                macOn = Prefs.DEFAULT_MAC_ENABLED,
                userAgentOn = Prefs.DEFAULT_USER_AGENT_ENABLED,
                model = model,
                country = country,
                imei = "",
                mac = "",
                eid = "",
                userAgent = defaultUserAgent(model),
                iccid = "",
                imsi = "",
                phone = ""
            )
        }
    }

    /**
     * Activation marker (v1.0.25): tell the Device Mask app the module
     * ran, via an explicit broadcast — never via RemotePreferences.
     *
     * v1.0.24 wrote the marker with getRemotePreferences(...).edit(),
     * but LSPosed's RemotePreferences is read-only: edit() throws
     * UnsupportedOperationException ("Read only implementation"), caught
     * silently, so the activation card could NEVER turn green. The
     * broadcast goes to ActiveEventReceiver in the app's own process,
     * which writes the same KEY_LAST_ACTIVE_TIME into the same prefs
     * file MainActivity reads. Only the time is sent — no settings.
     *
     * v1.0.28 adds a second channel: the same marker is also written
     * through the app's ActivationProvider ContentProvider, so the card
     * turns green even if the broadcast is dropped. Either channel
     * alone is enough. Best-effort: a failure here never breaks the
     * target app.
     */
    private fun markActive(app: Context) {
        try {
            val broadcast = Intent(Prefs.ACTION_MODULE_ACTIVE).apply {
                // Fully explicit: both package and component, so the
                // broadcast is delivered to the manifest receiver on
                // every Android version.
                setPackage("com.device.mask")
                setComponent(
                    ComponentName(
                        "com.device.mask",
                        "com.device.mask.ActiveEventReceiver"
                    )
                )
                putExtra(Prefs.EXTRA_ACTIVE_TIME, System.currentTimeMillis())
            }
            app.sendBroadcast(broadcast)
        } catch (e: Throwable) {
            // Marker failure must never break the target app.
        }
        // Second channel (v1.0.28): same marker, same time, through
        // the app's ContentProvider. Best-effort, never break the app.
        try {
            val values = ContentValues().apply {
                put("time", System.currentTimeMillis())
            }
            app.contentResolver.insert(
                Uri.parse("content://com.device.mask.activation/activate"),
                values
            )
        } catch (e: Throwable) {
            // Marker failure must never break the target app.
        }
    }

    /** Hook a method only if it exists on this Android version. */
    private fun hookIfExists(clazz: Class<*>, name: String, vararg params: Class<*>) =
        try {
            val method = clazz.getDeclaredMethod(name, *params)
            hook(method)
        } catch (e: Throwable) {
            // NoSuchMethodException / ClassNotFoundException / anything
            // else: this Android version does not have the method, so
            // there is nothing to hook. Fail silently.
            null
        }

    /** Set a Build.* field by reflection (fields cannot be hooked). */
    private fun setBuildField(name: String, value: String) {
        try {
            val field = Build::class.java.getDeclaredField(name)
            field.isAccessible = true
            field.set(null, value)
        } catch (e: Throwable) {
            // Field missing or not settable on this version: ignore.
        }
    }

    /** Apply the chosen model to the Build.* fields of this process. */
    private fun applyDeviceModel(model: DeviceModel) {
        setBuildField("MODEL", model.modelName)
        setBuildField("MANUFACTURER", model.manufacturer)
        setBuildField("BRAND", model.brand)
        setBuildField("DEVICE", model.codename)
        setBuildField("PRODUCT", model.codename)
        setBuildField("FINGERPRINT", model.fingerprint)
        setBuildField("BOARD", model.codename)
        setBuildField("HARDWARE", model.codename)
        setBuildField("ID", model.buildId())
        setBuildField("DISPLAY", model.buildId())
    }

    private fun parseMacBytes(mac: String): ByteArray? {
        return try {
            val parts = mac.split(":")
            if (parts.size != 6) return null
            ByteArray(6) { parts[it].toInt(16).toByte() }
        } catch (e: Throwable) {
            null
        }
    }

    override fun onPackageReady(param: PackageReadyParam) {
        super.onPackageReady(param)

        if (!param.isFirstPackage) {
            return
        }

        val settings = readSettings()

        // Prove the module ran: send the activation broadcast from the
        // target app's Application.onCreate (same pattern SureSIM uses
        // to reach the ClipboardManager). onPackageReady itself is too
        // early — the Application object does not exist yet there.
        hookIfExists(
            Application::class.java,
            "onCreate"
        )?.intercept { chain ->
            try {
                val app = chain.thisObject as? Application
                if (app != null) {
                    markActive(app)
                }
            } catch (e: Throwable) {
                // Never crash the target app.
            }
            chain.proceed()
        }

        // Second trigger (v1.0.26): Application.attach is final, so it
        // fires for every app process even when the app's Application
        // subclass never calls super.onCreate() — the onCreate hook
        // above silently never runs for those apps. markActive runs
        // after proceed, once the base context is attached.
        hookIfExists(
            Application::class.java,
            "attach",
            Context::class.java
        )?.intercept { chain ->
            val result = chain.proceed()
            try {
                val app = chain.thisObject as? Application
                if (app != null) {
                    markActive(app)
                }
            } catch (e: Throwable) {
                // Never crash the target app.
            }
            result
        }

        // 1. Device identity — Build.* fields (reflection, not hooks).
        if (settings.deviceOn) {
            applyDeviceModel(settings.model)
        }

        // Some apps read ro.product.* system properties instead of the
        // Build fields. android.os.SystemProperties is hidden, so it is
        // looked up by name and hooked only if present.
        try {
            val systemProperties =
                Class.forName("android.os.SystemProperties")
            hookIfExists(systemProperties, "get", String::class.java)
                ?.intercept { chain ->
                    val key = chain.getArg(0) as? String
                    if (!settings.deviceOn) {
                        return@intercept chain.proceed()
                    }
                    when (key) {
                        "ro.product.model" -> settings.model.modelName
                        "ro.product.manufacturer" -> settings.model.manufacturer
                        "ro.product.brand" -> settings.model.brand
                        "ro.product.device" -> settings.model.codename
                        "ro.product.name" -> settings.model.codename
                        "ro.build.fingerprint" -> settings.model.fingerprint
                        "ro.build.id" -> settings.model.buildId()
                        else -> chain.proceed()
                    }
                }
        } catch (e: Throwable) {
            // SystemProperties not present on this version: ignore.
        }

        // 2. eSIM support: make the app think eSIM is supported.
        hookIfExists(
            EuiccManager::class.java,
            "isEnabled"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.esimOn) {
                return@intercept chain.proceed()
            }
            chain.proceed()
            true
        }

        // 3. EID: the eSIM identifier shown to apps.
        hookIfExists(
            EuiccManager::class.java,
            "getEid"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.eidOn || s.eid.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.eid
        }

        // 4. Network / SIM: operator, MCC/MNC, SIM serial, IMSI, phone.
        hookIfExists(
            TelephonyManager::class.java,
            "getNetworkOperatorName"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn) return@intercept chain.proceed()
            s.country.operator
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getNetworkOperator"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn) return@intercept chain.proceed()
            s.country.mcc + s.country.mnc
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getSimOperator"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn) return@intercept chain.proceed()
            s.country.mcc + s.country.mnc
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getSimOperatorName"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn) return@intercept chain.proceed()
            s.country.operator
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getSimSerialNumber"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn || s.iccid.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.iccid
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getSubscriberId"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn || s.imsi.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.imsi
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getLine1Number"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.networkOn || s.phone.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.phone
        }

        // 5. Country / locale / timezone / GPS.
        hookIfExists(
            TelephonyManager::class.java,
            "getSimCountryIso"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.locationOn) return@intercept chain.proceed()
            s.country.iso.lowercase()
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getNetworkCountryIso"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.locationOn) return@intercept chain.proceed()
            s.country.iso.lowercase()
        }
        hookIfExists(
            Locale::class.java,
            "getDefault"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.locationOn) return@intercept chain.proceed()
            Locale(s.country.language, s.country.iso)
        }
        hookIfExists(
            TimeZone::class.java,
            "getDefault"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.locationOn) return@intercept chain.proceed()
            TimeZone.getTimeZone(s.country.timezone)
        }
        hookIfExists(
            Location::class.java,
            "getLatitude"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.locationOn) return@intercept chain.proceed()
            s.country.latitude
        }
        hookIfExists(
            Location::class.java,
            "getLongitude"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.locationOn) return@intercept chain.proceed()
            s.country.longitude
        }

        // 6. IMEI.
        hookIfExists(
            TelephonyManager::class.java,
            "getDeviceId"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.imeiOn || s.imei.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.imei
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getDeviceId",
            Integer.TYPE
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.imeiOn || s.imei.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.imei
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getImei"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.imeiOn || s.imei.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.imei
        }
        hookIfExists(
            TelephonyManager::class.java,
            "getImei",
            Integer.TYPE
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.imeiOn || s.imei.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.imei
        }

        // 7. MAC address.
        hookIfExists(
            WifiInfo::class.java,
            "getMacAddress"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.macOn || s.mac.isEmpty()) {
                return@intercept chain.proceed()
            }
            s.mac
        }
        hookIfExists(
            NetworkInterface::class.java,
            "getHardwareAddress"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.macOn || s.mac.isEmpty()) {
                return@intercept chain.proceed()
            }
            val iface = chain.thisObject as? NetworkInterface
            val name = iface?.name ?: ""
            if (name.startsWith("wlan") || name.startsWith("eth") ||
                name.startsWith("p2p")
            ) {
                parseMacBytes(s.mac) ?: chain.proceed()
            } else {
                chain.proceed()
            }
        }

        // 8. WebView user-agent.
        hookIfExists(
            WebSettings::class.java,
            "getUserAgentString"
        )?.intercept { chain ->
            val s = readSettings()
            if (!s.userAgentOn) return@intercept chain.proceed()
            s.userAgent
        }
    }
}
