package moe.shizuku.manager.utils

import com.android.apksig.ApkVerifier
import java.io.File
import java.security.MessageDigest

/**
 * Verifies APKs that this fork installs over itself.
 *
 * The release APK is stored in `version.json` together with a SHA-256 digest, but both the
 * digest and the APK are hosted by the same infrastructure. Pinning the fork's CI signing
 * certificate provides an independent check: even a compromised/misconfigured release host
 * can't install a differently-signed APK over this app.
 */
object ApkSignature {

    // SHA-256 (lowercase hex) of the DER-encoded X.509 certificate used by the fork's release
    // keystore (see .github/workflows/app.yml). Verified from shizuku-v13.9.1-dwicao.apk.
    private const val EXPECTED_CERT_SHA256 =
        "b435cbb47bd8e93a208104aec55f9fd6b7aa3923415eef316273d29f871b7f62"

    fun verifyUpdateApk(file: File) {
        val result = ApkVerifier.Builder(file).build().verify()
        if (!result.isVerified) {
            throw SecurityException("Downloaded APK failed signature verification")
        }

        val expected = EXPECTED_CERT_SHA256.lowercase()
        val matches = result.signerCertificates.any { cert ->
            cert.encoded.digest() == expected
        }
        if (!matches) {
            throw SecurityException("Downloaded APK is not signed by this fork's release key")
        }
    }

    private fun ByteArray.digest(): String =
        MessageDigest.getInstance("SHA-256")
            .digest(this)
            .joinToString("") { "%02x".format(it) }
}
