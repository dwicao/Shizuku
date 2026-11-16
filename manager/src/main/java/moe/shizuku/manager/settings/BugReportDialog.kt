package moe.shizuku.manager.settings

import android.app.Dialog
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import moe.shizuku.manager.BuildConfig
import moe.shizuku.manager.R
import moe.shizuku.manager.databinding.BugReportDialogBinding
import moe.shizuku.manager.ktx.asLink
import moe.shizuku.manager.ktx.applyTemplateArgs
import moe.shizuku.manager.utils.CustomTabsHelper
import moe.shizuku.manager.worker.AdbStartWorker
import moe.shizuku.manager.utils.CrashLog

class BugReportDialog : DialogFragment() {

    private lateinit var binding: BugReportDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        binding = BugReportDialogBinding.inflate(layoutInflater)

        val updateLink = getString(R.string.bug_report_dialog_link_update)
            .asLink("https://github.com/dwicao/Shizuku/releases/latest")

        val wikiLink = getString(R.string.bug_report_dialog_link_wiki)
            .asLink("https://github.com/dwicao/Shizuku/issues?q=is%3Aissue+label%3Abug")

        val issuesLink = getString(R.string.bug_report_dialog_link_issues)
            .asLink("https://github.com/dwicao/Shizuku/issues")

        binding.apply {
            updateText.applyTemplateArgs(updateLink)
            wikiText.applyTemplateArgs(wikiLink)
            issuesText.applyTemplateArgs(issuesLink)
            methodText.applyTemplateArgs("GitHub")
        }

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.settings_report_bug)
            .setView(binding.root)
            .setPositiveButton("GitHub") { _, _ ->
                val body = CrashLog.buildMarkdown(context)
                val issueUrl = "https://github.com/dwicao/Shizuku/issues/new" +
                    "?body=" + Uri.encode("Please describe the bug. Include steps to reproduce if possible.\n\n" + body)
                CustomTabsHelper.launchUrlOrCopy(context, issueUrl)
            }
            .setNegativeButton(R.string.bug_report_dialog_button_copy) { _, _ ->
                val body = CrashLog.buildMarkdown(context)
                try {
                    context.getSystemService(android.content.ClipboardManager::class.java).setText(android.content.ClipData.newPlainText("crash log", body))
                    Toast.makeText(context, context.getString(R.string.toast_copied_to_clipboard), Toast.LENGTH_SHORT).show()
                } catch (_: Throwable) {
                    Toast.makeText(context, context.getString(R.string.bug_report_dialog_copy_failed), Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val nm = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(AdbStartWorker.NOTIFICATION_ID)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (activity is BugReportDialogActivity) activity?.finish()
    }

}