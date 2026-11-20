package com.device.mask

import android.content.Context
import android.content.pm.PackageManager
import java.security.MessageDigest

/**
 * Verifies this app's own signing certificate against the pinned debug
 * keystore (Prefs.EXPECTED_SIGNATURE_SHA256).
 *
 * The release APKs are signed with the project's debug key. If the app
 * is re-signed with any other key, the signature check fails and the
 * app refuses to open.
 */
object SignatureCheck {

    fun isSignatureValid(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            val info = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signingInfo = info.signingInfo ?: return false
            val signatures = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
            if (signatures.isEmpty()) return false
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(signatures[0].toByteArray())
            val hex = hash.joinToString("") { "%02X".format(it) }
            hex.equals(Prefs.EXPECTED_SIGNATURE_SHA256, ignoreCase = true)
        } catch (e: Throwable) {
            false
        }
    }
}
