package com.device.mask

/**
 * Shared constants for Device Mask.
 *
 * The settings UI (MainActivity) writes these SharedPreferences; the hook
 * (ModuleMain) reads them from the target app's process through the
 * framework's RemotePreferences (getRemotePreferences). No world-readable
 * file is needed.
 *
 * Free tier: only the first [FREE_MODEL_COUNT] models and the first
 * [FREE_COUNTRY_COUNT] countries are selectable. Everything after that
 * is shown as locked in the UI. There is no premium unlock, login or
 * redeem-code code anywhere in this app — premium is simply not included.
 *
 * The app also verifies its own signing certificate at startup against
 * [EXPECTED_SIGNATURE_SHA256] (the pinned debug keystore). If the
 * signature does not match, the app refuses to open.
 */
object Prefs {
    const val FILE_NAME = "device_mask"

    // --- Feature toggles (each feature has its own on/off switch) ---
    const val KEY_DEVICE_ENABLED = "device_enabled"
    const val KEY_LOCATION_ENABLED = "location_enabled"
    const val KEY_NETWORK_ENABLED = "network_enabled"
    const val KEY_EID_ENABLED = "eid_enabled"
    const val KEY_ESIM_ENABLED = "esim_enabled"
    const val KEY_IMEI_ENABLED = "imei_enabled"
    const val KEY_MAC_ENABLED = "mac_enabled"
    const val KEY_USER_AGENT_ENABLED = "user_agent_enabled"

    const val DEFAULT_DEVICE_ENABLED = true
    const val DEFAULT_LOCATION_ENABLED = true
    const val DEFAULT_NETWORK_ENABLED = true
    const val DEFAULT_EID_ENABLED = false
    const val DEFAULT_ESIM_ENABLED = true
    const val DEFAULT_IMEI_ENABLED = false
    const val DEFAULT_MAC_ENABLED = false
    const val DEFAULT_USER_AGENT_ENABLED = false

    // --- Selections ---
    const val KEY_SELECTED_MODEL = "selected_model"
    const val KEY_SELECTED_COUNTRY = "selected_country"
    const val DEFAULT_SELECTED_MODEL = 0
    const val DEFAULT_SELECTED_COUNTRY = 0

    // --- Generated identity values (regenerated when the model or
    // country changes, or when Random-all is tapped) ---
    const val KEY_IMEI = "generated_imei"
    const val KEY_MAC = "generated_mac"
    const val KEY_EID = "generated_eid"
    const val KEY_USER_AGENT = "generated_user_agent"
    const val KEY_ICCID = "generated_iccid"
    const val KEY_IMSI = "generated_imsi"
    const val KEY_PHONE = "generated_phone"

    // --- Activation marker: the hook writes the current time here every
    // time it runs in a target app, so the UI can prove the module is
    // actually loaded (green Activated) instead of guessing.
    const val KEY_LAST_ACTIVE_TIME = "last_active_time"

    // --- Activation broadcast (v1.0.25): the hook CANNOT write the marker
    // through RemotePreferences — LSPosed's RemotePreferences.edit()
    // throws UnsupportedOperationException ("Read only implementation"),
    // so v1.0.24's card could never turn green. Instead the hook sends
    // this explicit broadcast (package-restricted to com.device.mask)
    // and ActiveEventReceiver records the time in the app's own prefs.
    // Only the time is sent — no settings or identity values.
    const val ACTION_MODULE_ACTIVE = "com.device.mask.ACTION_MODULE_ACTIVE"
    const val EXTRA_ACTIVE_TIME = "active_time"

    // --- Free tier limits ---
    const val FREE_MODEL_COUNT = 10
    const val FREE_COUNTRY_COUNT = 10

    // --- Signature check ---
    // SHA-256 of the pinned debug keystore (app/debug.keystore,
    // standard debug credentials). Must match the key the APK is signed
    // with or the app refuses to open.
    const val EXPECTED_SIGNATURE_SHA256 =
        "4579987292BC2C125D5772E0257ED4307816DA1A6FDA5E0310D8A4EFDC2F3FAB"
}
