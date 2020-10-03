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

package com.crrl.beatplayer.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jetbrains.annotations.NotNull
import timber.log.Timber

class ReleaseTree : @NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        try {
            if (t != null) {
                if (tag != null) {
                    crashlytics.setCustomKey("beatplayer_crash_tag", tag)
                }
                crashlytics.log(t.toString())
            } else {
                crashlytics.log(priority.toString() + tag + message)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}