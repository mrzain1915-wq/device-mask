package com.device.mask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives the hook's "module ran in a target app" broadcast and records
 * only the time it happened, in the app's own SharedPreferences — the
 * same file MainActivity reads for the activation card.
 *
 * This exists because the hook cannot write the marker itself:
 * LSPosed's RemotePreferences.edit() throws UnsupportedOperationException
 * ("Read only implementation"), so v1.0.24's activation card could never
 * turn green. A broadcast from the hooked app's process to this receiver
 * (same pattern as SureSIM's CopyEventReceiver) is the working route.
 * Only the time is sent in the broadcast — no settings or identity data.
 */
class ActiveEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Prefs.ACTION_MODULE_ACTIVE) return
        val time = intent.getLongExtra(Prefs.EXTRA_ACTIVE_TIME, 0L)
        if (time <= 0L) return
        try {
            context.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(Prefs.KEY_LAST_ACTIVE_TIME, time)
                .apply()
        } catch (e: Throwable) {
            // Recording the time is best-effort only.
        }
    }
}
