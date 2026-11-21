package com.device.mask

import android.content.Context
import java.security.MessageDigest
import java.util.Locale

/**
 * Local account + premium storage.
 *
 * A single local account at a time, stored in SharedPreferences file
 * "device_mask_account". The password is NEVER stored in plaintext —
 * only its SHA-256 hex hash. Premium is unlocked only by a valid
 * redeem code (validated against the hashed codes in Prefs) and is
 * tied to the logged-in account: logging out clears premium, and a
 * different account logging in does not inherit it.
 *
 * There are no network calls anywhere — login and redeem are fully
 * local. There is no "unlock all" button or debug bypass: premium
 * requires a valid redeem code.
 */
object AccountStore {
    private const val FILE_NAME = "device_mask_account"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD_HASH = "password_hash"
    private const val KEY_PREMIUM_EMAIL = "premium_email"
    private const val KEY_PREMIUM_ACTIVE = "premium_active"
    private const val KEY_USED_CODE_HASHES = "used_code_hashes"

    fun sha256Hex(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    /** Email of the logged-in account, or null when logged out. */
    fun loggedInEmail(context: Context): String? =
        prefs(context).getString(KEY_EMAIL, null)?.takeIf { it.isNotEmpty() }

    /** True when premium is active for the currently logged-in account. */
    fun isPremium(context: Context): Boolean {
        val p = prefs(context)
        if (!p.getBoolean(KEY_PREMIUM_ACTIVE, false)) return false
        val premiumEmail = p.getString(KEY_PREMIUM_EMAIL, null)
        val email = loggedInEmail(context)
        return premiumEmail != null && email != null && premiumEmail == email
    }

    /** The account premium is active on, or null. */
    fun premiumEmail(context: Context): String? =
        prefs(context).getString(KEY_PREMIUM_EMAIL, null)?.takeIf { it.isNotEmpty() }

    /**
     * Log in. Stores the email as plain text and ONLY the SHA-256 hash
     * of the password. Validation (email contains "@", password at
     * least 4 characters) happens in the UI. Premium is kept only when
     * the same account that redeemed it logs back in; a different
     * account starts without premium.
     */
    fun login(context: Context, email: String, password: String) {
        val cleanEmail = email.trim()
        val p = prefs(context)
        val keepPremium = p.getBoolean(KEY_PREMIUM_ACTIVE, false) &&
            p.getString(KEY_PREMIUM_EMAIL, null) == cleanEmail
        val editor = p.edit()
            .putString(KEY_EMAIL, cleanEmail)
            .putString(KEY_PASSWORD_HASH, sha256Hex(password))
        if (keepPremium) {
            editor.putBoolean(KEY_PREMIUM_ACTIVE, true)
        } else {
            editor.remove(KEY_PREMIUM_EMAIL).putBoolean(KEY_PREMIUM_ACTIVE, false)
        }
        editor.apply()
    }

    /** Log out: clears the account and its premium. */
    fun logout(context: Context) {
        prefs(context).edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD_HASH)
            .remove(KEY_PREMIUM_EMAIL)
            .putBoolean(KEY_PREMIUM_ACTIVE, false)
            .apply()
    }

    /**
     * Validate a redeem code. The entered code is uppercased and
     * trimmed, hashed with SHA-256 and compared to the stored hashes —
     * the plain codes never appear in the app. Each code works for ONE
     * account only: once redeemed, its hash is recorded as used and
     * the same code cannot activate premium for another account.
     * On success, premium is activated for the logged-in account.
     * Returns false when not logged in, the code is invalid, or the
     * code was already used.
     */
    fun redeem(context: Context, code: String): Boolean {
        val email = loggedInEmail(context) ?: return false
        val normalized = code.trim().uppercase(Locale.getDefault())
        val hash = sha256Hex(normalized)
        if (hash !in Prefs.REDEEM_CODE_HASHES) return false
        val p = prefs(context)
        val used = p.getStringSet(KEY_USED_CODE_HASHES, emptySet()) ?: emptySet()
        if (hash in used) return false
        val newUsed = used.toMutableSet().apply { add(hash) }
        p.edit()
            .putString(KEY_PREMIUM_EMAIL, email)
            .putBoolean(KEY_PREMIUM_ACTIVE, true)
            .putStringSet(KEY_USED_CODE_HASHES, newUsed)
            .apply()
        return true
    }
}
