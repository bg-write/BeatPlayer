/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    val isExtraInfo: LiveData<Boolean>
        get() {
            launch {
                val isEnable = withContext(IO) {
                    settingsUtility.isExtraInfo
                }
                isExtraInfoLiveData.postValue(isEnable)
            }
            return isExtraInfoLiveData
        }

    private val isExtraActionsLiveData = MutableLiveData<Boolean>()
    val isExtraActions: LiveData<Boolean>
        get() {
            launch {
                val isEnable = withContext(IO) {
                    settingsUtility.isExtraAction
                }
                isExtraActionsLiveData.postValue(isEnable)
            }
            return isExtraActionsLiveData
        }

    private val forwardRewindTimeData = MutableLiveData<Int>()
    val forwardRewindTime: LiveData<Int>
        get() {
            launch {
                val time = withContext(IO) {
                    settingsUtility.forwardRewindTime
                }
                forwardRewindTimeData.postValue(time)
            }
            return forwardRewindTimeData
        }
}