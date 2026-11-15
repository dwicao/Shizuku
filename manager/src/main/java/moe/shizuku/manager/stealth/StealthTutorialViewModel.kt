package moe.shizuku.manager.stealth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.shizuku.manager.utils.ApkUtils.*

sealed class UiState {
    data class Idle(
        val action: Action,
    ) : UiState()

    object Loading : UiState()

    data class PendingInstall(
        val apk: java.io.File,
    ) : UiState()

    data class Error(
        val error: Exception,
    ) : UiState()
}

enum class Action {
    INSTALL_STUB,
    UNINSTALL_STUB,
}

class StealthTutorialViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val app: Application = getApplication()
    private val appContext = app.applicationContext

    private val _uiState = MutableLiveData<UiState>(UiState.Idle(Action.UNINSTALL_STUB))
    val uiState: LiveData<UiState> = _uiState

    init {
        refresh()
    }

    private fun isStubInstalled() =
        runCatching {
            appContext.packageManager.getPackageInfo(ORIGINAL_PACKAGE_NAME, 0)
        }.isSuccess

    fun refresh() {
        _uiState.value = UiState.Idle(
            if (isStubInstalled()) Action.UNINSTALL_STUB else Action.INSTALL_STUB
        )
    }

    fun onInstallStub() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.postValue(UiState.Loading)
                // The stub carries the original package name and forwards everything to this app.
                val apk = createStubApk(app.packageName)
                _uiState.postValue(UiState.PendingInstall(apk))
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error(e))
                Log.e("StealthTutorialViewModel", "Error creating stub APK", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        workDir.deleteRecursively()
    }
}
