package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import com.crrl.beatplayer.utils.SettingsUtility
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val settingsUtility: SettingsUtility,
) : CoroutineViewModel(Main) {
    private val isExtraInfoLiveData = MutableLiveData<Boolean>()
    fun isExtraInfo(): LiveData<Boolean> {
        launch {
            val isEnable = withContext(IO) {
                settingsUtility.isExtraInfo
            }
            isExtraInfoLiveData.postValue(isEnable)
        }
        return isExtraInfoLiveData
    }

    private val isExtraActionsLiveData = MutableLiveData<Boolean>()
    fun isExtraActions(): LiveData<Boolean> {
        launch {
            val isEnable = withContext(IO) {
                settingsUtility.isExtraAction
            }
            isExtraActionsLiveData.postValue(isEnable)
        }
        return isExtraActionsLiveData
    }
}