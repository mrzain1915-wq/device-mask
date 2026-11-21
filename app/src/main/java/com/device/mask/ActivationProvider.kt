package com.device.mask

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri

/**
 * Second activation channel (v1.0.28): the hook writes the "module ran"
 * marker through this ContentProvider as well as the broadcast, so the
 * activation card turns green even if the broadcast is dropped.
 *
 * The hook (running in a target app's process) calls
 * contentResolver.insert(Uri.parse("content://com.device.mask.activation/activate"),
 * ContentValues with "time"). Only the "activate" path is accepted and
 * only the time is written — no settings or identity values. The
 * provider writes it into the same SharedPreferences file/key
 * MainActivity reads (Prefs.FILE_NAME / Prefs.KEY_LAST_ACTIVE_TIME),
 * exactly like ActiveEventReceiver does for the broadcast.
 *
 * The provider is exported because the call comes from a hooked target
 * app's process, not from this app. Everything is try/catch guarded: a
 * failure here must never crash the calling (target) app.
 */
class ActivationProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        try {
            if (uri.lastPathSegment != "activate") return null
            val time = try {
                values?.getAsLong("time") ?: System.currentTimeMillis()
            } catch (e: Throwable) {
                System.currentTimeMillis()
            }
            val safeTime = if (time > 0L) time else System.currentTimeMillis()
            val context: Context = context ?: return null
            context.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(Prefs.KEY_LAST_ACTIVE_TIME, safeTime)
                .apply()
            DiagLog.log(context, "Activation received via provider — module ran")
        } catch (e: Throwable) {
            // Never crash the calling app.
        }
        return uri
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0

    override fun getType(uri: Uri): String? = null
}
