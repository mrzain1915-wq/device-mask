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
 * is shown as locked in the UI unless Premium is active.
 *
 * Premium is unlocked with a redeem code (see [REDEEM_CODE_HASHES]):
 * the owner holds the plain codes and gives them out. Only the SHA-256
 * hashes of the codes are stored in this app — the plain codes never
 * appear here. Premium is tied to the logged-in account (AccountStore)
 * and unlocks all models and countries. There is no server, no
 * "unlock all" button and no debug bypass.
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

    // --- Premium redeem codes ---
    // Only the SHA-256 hex hashes of the 10 redeem codes are stored
    // here. A code is valid when SHA-256 of the entered code
    // (uppercased, trimmed, format DM-XXXX-XXXX) matches one of these
    // hashes. The plain codes are held by the owner and given out to
    // users; they never appear in this app's source or APK.
    val REDEEM_CODE_HASHES = setOf(
        "116ee38aae36b3e6d3668b7837f415c507e43e4ad0b996c2121a880886bba960",
        "43ce179f51ab31708c9d474298b3fa6ee848cc4b917ce96ce24a6ded0ae4595f",
        "d9a3dd2887f8a6b14b710550e6275fbbe5231f701728af029801923bb18a845b",
        "7e5089ee0707b9f3383c1f7d2b0897e67217fe094e03cfa4cdd64d5a15603209",
        "d80e31e7176c39b336e89da01a244839f9cef7ce0fcaffe14a3e8520d90d52b9",
        "cc0414940a80d89cf575164c495b12430f0a43761b1a886afbd479aa8107ecb3",
        "1c77645404e44bcc208cad8804ec327aa35bcf6d91cc08f80696fcf35706740c",
        "c41aa856cc9f044becdce8624ff0ea497fbe50c03d6b9a11d9743091b7539244",
        "bd04aaa702ae30fe0af30245d97cbe3a59c6fddb0109e17f3b0b8f274988fe70",
        "3efd1911349296d392265f381352b158120e1c0261be44b03878d68e3c17db5a"
    )

    // --- Signature check ---
    // SHA-256 of the pinned debug keystore (app/debug.keystore,
    // standard debug credentials). Must match the key the APK is signed
    // with or the app refuses to open.
    const val EXPECTED_SIGNATURE_SHA256 =
        "4579987292BC2C125D5772E0257ED4307816DA1A6FDA5E0310D8A4EFDC2F3FAB"
}
