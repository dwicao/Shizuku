package moe.shizuku.manager.utils

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Last-resort crash recorder. Captures only the app's own crashes and never sends anything
 * over the network; the user chooses to share the file when filing a bug report.
 */
object CrashLog {
    private const val DIR_NAME = "crash"
    private const val MAX_FILES = 5

    fun install(context: Context) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { write(context.applicationContext, thread.name, throwable) }
            previous?.uncaughtException(thread, throwable)
        }
    }

    fun latestFile(context: Context): File? =
        crashDir(context)
            ?.listFiles()
            ?.filter { it.isFile }
            ?.maxByOrNull { it.lastModified() }

    fun buildMarkdown(context: Context): String {
        val file = latestFile(context)
        val header = buildString {
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Shizuku: ${moe.shizuku.manager.BuildConfig.VERSION_NAME} (${moe.shizuku.manager.BuildConfig.VERSION_CODE})")
            if (file != null) {
                appendLine()
                appendLine("Latest crash log (${file.name}):")
                appendLine("```")
                appendLine(file.readText().takeLast(8000))
                appendLine("```")
            }
        }
        return header.trim()
    }

    private fun crashDir(context: Context): File? =
        context.filesDir.resolve(DIR_NAME).apply { mkdirs() }.takeIf { it.exists() }

    private fun write(context: Context, threadName: String, throwable: Throwable) {
        val dir = crashDir(context) ?: return

        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val file = File(dir, "crash-$timestamp.txt")
        file.writeText(buildString {
            appendLine("Thread: $threadName")
            appendLine()
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            append(sw.toString())
        })

        dir.listFiles()
            ?.filter { it.isFile }
            ?.sortedByDescending { it.lastModified() }
            ?.drop(MAX_FILES)
            ?.forEach { it.delete() }
    }
}
