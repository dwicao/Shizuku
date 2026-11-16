package moe.shizuku.manager.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import moe.shizuku.manager.BuildConfig
import moe.shizuku.manager.R
import moe.shizuku.manager.ShizukuApplication
import moe.shizuku.manager.ShizukuSettings
import moe.shizuku.manager.utils.ApkUtils.*
import moe.shizuku.manager.utils.ApkSignature
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.security.MessageDigest

object UpdateHelper {
    private val app = ShizukuApplication.application
    private val appContext = ShizukuApplication.appContext

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var latestRelease: Release

    suspend fun checkAndInstallUpdates() {
        if (isUpdateAvailable()) {
            update()
        } else {
            Toast
                .makeText(
                    appContext,
                    appContext.getString(R.string.update_latest_installed),
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    fun isCheckForUpdatesEnabled(): Boolean = ShizukuSettings.getUpdateMode() != ShizukuSettings.UpdateMode.OFF

    suspend fun isNewUpdateAvailable(): Boolean {
        val lastPromptedVersion =
            Version.parse(ShizukuSettings.getLastPromptedVersion())
                ?: Version.parse(currentVersionName())
                ?: return false
        return if (isUpdateAvailable()) latestRelease.version > lastPromptedVersion else false
    }

    suspend fun isUpdateAvailable(): Boolean {
        try {
            val latest = fetchLatestRelease().version ?: return false
            val current = Version.parse(currentVersionName()) ?: return false
            return latest > current
        } catch (e: Exception) {
            android.util.Log.e("UpdateHelper", "Failed to check for updates", e)
            Toast
                .makeText(
                    appContext,
                    appContext.getString(R.string.update_check_failed),
                    Toast.LENGTH_SHORT,
                ).show()
            return false
        }
    }

    fun updateLastPromptedVersion() = ShizukuSettings.setLastPromptedVersion(latestRelease.version.toString())

    private fun currentVersionName(): String = BuildConfig.VERSION_NAME

    suspend fun update() {
        try {
            updateInternal()
        } catch (e: Exception) {
            android.util.Log.e("UpdateHelper", "Update failed", e)
            Toast
                .makeText(
                    appContext,
                    appContext.getString(R.string.update_failed),
                    Toast.LENGTH_LONG,
                ).show()
        }
    }

    private suspend fun updateInternal() {
        if (!::latestRelease.isInitialized && !isUpdateAvailable()) return

        Toast
            .makeText(
                appContext,
                appContext.getString(R.string.update_downloading),
                Toast.LENGTH_SHORT,
            ).show()

        val apk = latestRelease.download()
        if (apk == null) {
            Toast
                .makeText(
                    appContext,
                    appContext.getString(R.string.update_download_failed),
                    Toast.LENGTH_SHORT,
                ).show()
            return
        }

        // Fork builds prior to 13.9.0 used "moe.shizuku.privileged.api". An update APK from
        // version.json is always built for this package, but guard against a stale or wrong
        // entry anyway: installing a differently-named APK would silently create a second app
        // instead of updating.
        val pm = appContext.packageManager
        val apkPackageName = pm.getPackageArchiveInfo(apk.path, 0)?.packageName
        if (app.packageName != apkPackageName) {
            android.util.Log.e("UpdateHelper", "Update APK package ($apkPackageName) does not match this app (${app.packageName})")
            Toast
                .makeText(
                    appContext,
                    appContext.getString(R.string.update_incompatible_package),
                    Toast.LENGTH_LONG,
                ).show()
            return
        }

        appContext.installPackage(apk) { isSuccess, _ ->
            val toastMsg =
                if (isSuccess) appContext.getString(R.string.update_success)
                else appContext.getString(R.string.update_failed)
            Toast.makeText(appContext, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun fetchLatestRelease(): Release =
        withContext(Dispatchers.IO) {
            // version.json is committed to the repository (and rewritten by CI on every
            // release), so this avoids the GitHub Releases API and its rate limits.
            val url = "https://raw.githubusercontent.com/dwicao/Shizuku/master/version.json"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw Exception("Couldn't fetch version.json")

            val parsed = json.decodeFromString<VersionJson>(body)
            val channel =
                if (ShizukuSettings.getUpdateMode() == ShizukuSettings.UpdateMode.BETA) {
                    parsed.beta ?: parsed.stable
                } else {
                    parsed.stable ?: parsed.beta
                }
                    ?.forPackage(app.packageName)
                    ?: throw Exception("No releases in version.json")

            val version = Version.parse(channel.versionName) ?: throw Exception("Invalid versionName in version.json")
            val digest =
                if (channel.sha256.startsWith("sha256:")) channel.sha256 else "sha256:${channel.sha256}"
            val filename = channel.apkUrl.substringAfterLast('/').ifBlank { "update.apk" }

            Release(
                version = version,
                filename = filename,
                url = channel.apkUrl,
                digest = digest,
            ).also { latestRelease = it }
        }

    private suspend fun Release.download(): File =
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("Download failed with HTTP ${response.code}")
            }

            // Download to filesDir instead of cacheDir: cacheDir may be reclaimed by the
            // system at any time, which would break the install that follows.
            val apkFile = File(appContext.filesDir, "update.apk")
            apkFile.outputStream().use { out ->
                response.body?.byteStream()?.copyTo(out)
            }

            val downloadedDigest = "sha256:" + apkFile.sha256()
            if (!downloadedDigest.equals(digest, ignoreCase = true)) {
                apkFile.delete()
                throw SecurityException("Digest of downloaded file does not match version.json")
            }

            try {
                ApkSignature.verifyUpdateApk(apkFile)
            } catch (e: Exception) {
                apkFile.delete()
                throw e
            }

            apkFile
        }

    fun File.sha256(): String {
        val bytes = readBytes()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

}
