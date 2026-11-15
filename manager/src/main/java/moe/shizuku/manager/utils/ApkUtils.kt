package moe.shizuku.manager.utils.ApkUtils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import com.reandroid.apk.ApkModule
import com.reandroid.common.Namespace
import moe.shizuku.manager.ShizukuApplication
import moe.shizuku.manager.utils.ApkSigner
import java.io.File

private const val TAG = "ApkUtils"

const val ORIGINAL_PACKAGE_NAME = "moe.shizuku.privileged.api"

private val app = ShizukuApplication.application
private val appContext = ShizukuApplication.appContext

val workDir by lazy {
    File(appContext.cacheDir, "patcher").also {
        it.deleteRecursively()
        it.mkdirs()
    }
}

fun createStubApk(clonePackageName: String): File {
    Log.i(TAG, "Extracting embedded stub APK")
    val original = File(workDir, "stub-original.apk")
    appContext.assets.open("stub.apk").use { input ->
        original.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    Log.i(TAG, "Patching clone package name in stub")
    val module = ApkModule.loadApkFile(original)
    val manifest = module.androidManifest
    val application = manifest.getApplicationElement()

    // The stub forwards launches, permission requests and provider calls to the hidden
    // Shizuku build. It learns the clone's package name from a meta-data entry that is
    // patched in here.
    var patched = false
    for (element in application.getElements("meta-data")) {
        val name = element.searchAttribute(Namespace.URI_ANDROID, "name")?.valueAsString
        if (name == CLONE_META_DATA) {
            element.searchAttribute(Namespace.URI_ANDROID, "value")?.setValueAsString(clonePackageName)
            patched = true
        }
    }

    if (!patched) {
        throw IllegalStateException("Stub APK is missing the $CLONE_META_DATA meta-data entry")
    }

    val outFile = File(appContext.filesDir, "stub.apk")
    return module.buildAndSign(outFile, maybeCreateSigningKey = true)
}

private const val CLONE_META_DATA = "moe.shizuku.stub.CLONE_PACKAGE"

private fun ApkModule.buildAndSign(
    outFile: File,
    maybeCreateSigningKey: Boolean = false
): File {
    Log.i(TAG, "Building new APK")
    val unsignedApk = File(workDir, "unsigned.apk")
    writeApk(unsignedApk)

    Log.i(TAG, "Signing APK")
    val key = ApkSigner.getSigningKey(maybeCreateSigningKey)
    ApkSigner.sign(unsignedApk, outFile, key)

    return outFile
}

fun Context.installPackage(
    apk: File,
    cb: ((Boolean, String?) -> Unit)? = null
) {
    val installer = appContext.packageManager.packageInstaller

    val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
    val sessionId = installer.createSession(sessionParams)
    val session = installer.openSession(sessionId)

    apk.inputStream().use { input ->
        session.openWrite("base.apk", 0, apk.length()).use { output ->
            input.copyTo(output)
            session.fsync(output)
        }
    }

    val pendingIntent = createInstallerPendingIntent(sessionId, cb)

    session.commit(pendingIntent.intentSender)
    session.close()
}

fun Context.uninstallPackage(
    pkgName: String,
    cb: ((Boolean, String?) -> Unit)? = null,
) {
    val installer = appContext.packageManager.packageInstaller
    val pendingIntent = createInstallerPendingIntent(0, cb)
    installer.uninstall(pkgName, pendingIntent.intentSender)
}

private var callback: ((Boolean, String?) -> Unit)? = null

val installerReceiver =
    object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    val confirmationIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                    if (confirmationIntent != null) {
                        context.startActivity(confirmationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }

                else -> {
                    val isSuccess = (status == PackageInstaller.STATUS_SUCCESS)
                    callback?.invoke(isSuccess, msg)
                    context.unregisterReceiver(this)
                }
            }
        }
    }

private fun Context.createInstallerPendingIntent(
    sessionId: Int,
    cb: ((Boolean, String?) -> Unit)? = null
): PendingIntent {
    callback = cb

    val installerAction = "${app.packageName}.INSTALLER_RESULT"
    val filter =
        IntentFilter().apply {
            addAction(installerAction)
        }
    registerReceiver(installerReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

    val callbackIntent =
        Intent(installerAction).apply {
            setPackage(app.packageName)
        }

    val flags =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

    return PendingIntent.getBroadcast(
        this,
        sessionId,
        callbackIntent,
        flags,
    )
}
