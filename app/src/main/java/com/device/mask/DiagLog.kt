package com.device.mask

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Small append-only diagnostics log, stored in its own SharedPreferences
 * file ("device_mask_diag") so it never touches the settings file the
 * hook reads. The newest 100 lines are kept; each line is
 * "HH:mm:ss — message". Shown on the Diagnose screen (newest first).
 *
 * Diagnostics must never break the app: every call is try/catch
 * guarded and best-effort only.
 */
object DiagLog {
    private const val FILE_NAME = "device_mask_diag"
    private const val KEY_LINES = "log_lines"
    private const val MAX_LINES = 100

    private fun timestamp(): String =
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    fun log(context: Context, message: String) {
        try {
            val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            val existing = prefs.getString(KEY_LINES, "") ?: ""
            val lines = existing.split("\n").filter { it.isNotEmpty() }.toMutableList()
            lines.add("${timestamp()} — $message")
            val trimmed = lines.takeLast(MAX_LINES)
            prefs.edit().putString(KEY_LINES, trimmed.joinToString("\n")).apply()
        } catch (e: Throwable) {
            // Diagnostics must never break the app.
        }
    }

    /** Newest first. */
    fun read(context: Context): List<String> {
        return try {
            val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            val raw = prefs.getString(KEY_LINES, "") ?: ""
            raw.split("\n").filter { it.isNotEmpty() }.reversed()
        } catch (e: Throwable) {
            emptyList()
        }
    }

    fun clear(context: Context) {
        try {
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY_LINES).apply()
        } catch (e: Throwable) {
            // Best-effort only.
        }
    }
}
