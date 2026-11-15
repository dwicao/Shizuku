package moe.shizuku.manager.stealth

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import moe.shizuku.manager.R
import moe.shizuku.manager.app.AppBarActivity
import moe.shizuku.manager.databinding.StealthTutorialActivityBinding
import moe.shizuku.manager.utils.ApkUtils.*
import rikka.core.util.ClipboardUtils

class StealthTutorialActivity : AppBarActivity() {
    private val viewModel: StealthTutorialViewModel by viewModels()
    private lateinit var binding: StealthTutorialActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StealthTutorialActivityBinding.inflate(layoutInflater, rootView, true)

        onBackPressedDispatcher.addCallback(this, backCallback)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(binding) {
            viewModel.uiState.observe(this@StealthTutorialActivity) { state ->
                val isLoading = (state is UiState.Loading)
                fab.isInvisible = isLoading
                loadingFab.isVisible = isLoading

                when (state) {
                    is UiState.Idle -> {
                        fab.update(state.action)
                        fab.setOnClickListener { onClick(state.action) }
                    }

                    is UiState.Loading -> null

                    is UiState.PendingInstall -> {
                        try {
                            installPackage(state.apk) { isSuccess, msg -> handleInstallerResult(isSuccess, msg) }
                        } catch (e: Exception) {
                            showErrorDialog(e)
                        }
                    }

                    is UiState.Error -> showErrorDialog(state.error)
                }
            }

            compatibilityCard.setOnClickListener { v ->
                val context = v.context
                if (
                    ClipboardUtils.put(context, codeSnippet) &&
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2
                ) {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.toast_copied_to_clipboard),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }

            ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
                val systemBarsInsets = insets.getInsets(Type.systemBars())
                val fabBottomMargin = systemBarsInsets.bottom + 16.dp

                listOf(fab, loadingFab).forEach {
                    it.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = fabBottomMargin
                    }
                }

                insets
            }
        }
    }

    override fun onDestroy() {
        runCatching {
            unregisterReceiver(installerReceiver)
        }
        super.onDestroy()
    }

    private fun onClick(action: Action) {
        when (action) {
            Action.INSTALL_STUB -> viewModel.onInstallStub()
            Action.UNINSTALL_STUB -> uninstallPackage(ORIGINAL_PACKAGE_NAME) { isSuccess, msg -> handleInstallerResult(isSuccess, msg) }
        }
    }

    private fun handleInstallerResult(isSuccess: Boolean, msg: String?) {
        viewModel.refresh()
        if (isSuccess) {
            Toast.makeText(
                this,
                getString(R.string.success),
                Toast.LENGTH_SHORT,
            ).show()
        } else showErrorDialog(Exception(msg ?: "Unknown error"))
    }

    private fun showErrorDialog(error: Exception) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.error)
            .setMessage(error.message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun ExtendedFloatingActionButton.update(action: Action) {
        if (action == Action.UNINSTALL_STUB) {
            setIconResource(R.drawable.ic_visibility_off_filled_24)
            text = getString(R.string.stealth_uninstall_stub)
        } else {
            setIconResource(R.drawable.ic_visibility_on_filled_24)
            text = getString(R.string.stealth_install_stub)
        }
    }

    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}

private const val codeSnippet = """
import android.content.Context
import rikka.shizuku.ShizukuProvider

private fun Context.shizukuPermission() =
    runCatching {
        packageManager.getPermissionInfo(ShizukuProvider.PERMISSION, 0)
    }.getOrNull()

fun Context.isShizukuInstalled() =
    shizukuPermission() != null

fun Context.getShizukuPackageName() =
    shizukuPermission()?.packageName
"""
