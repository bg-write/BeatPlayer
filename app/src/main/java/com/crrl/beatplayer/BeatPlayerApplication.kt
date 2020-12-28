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

package com.crrl.beatplayer

import android.app.Application
import com.crrl.beatplayer.BuildConfig.DEBUG
import com.crrl.beatplayer.notifications.notificationModule
import com.crrl.beatplayer.playback.playbackModule
import com.crrl.beatplayer.repository.repositoriesModule
import com.crrl.beatplayer.ui.viewmodels.viewModelModule
import com.crrl.beatplayer.utils.ReleaseTree
import com.crrl.beatplayer.utils.utilsModule
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jaudiotagger.tag.TagOptionSingleton
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class BeatPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        TagOptionSingleton.getInstance().isAndroid = true

        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        val modules = listOf(
            mainModel,
            notificationModule,
            playbackModule,
            repositoriesModule,
            viewModelModule,
            utilsModule
        )
        startKoin {
            androidContext(this@BeatPlayerApplication)
            modules(modules)
        }
    }
}