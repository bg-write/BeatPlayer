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

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.optimize
import com.crrl.beatplayer.extensions.systemService
import com.crrl.beatplayer.extensions.toFileDescriptor
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.BeatConstants.ARTWORK_URI
import com.crrl.beatplayer.utils.BeatConstants.SEEK_TO_POS
import com.crrl.beatplayer.utils.BeatConstants.SONG_LIST_NAME
import com.crrl.beatplayer.utils.BeatConstants.SONG_URI
import com.crrl.beatplayer.utils.SettingsUtility.Companion.QUEUE_INFO_KEY
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


object GeneralUtils {

    const val PORTRAIT = ORIENTATION_PORTRAIT

    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun getOrientation(context: Context): Int {
        return context.resources.configuration.orientation
    }

    fun formatMilliseconds(duration: Long): String {
        val seconds = (duration / 1000).toInt() % 60
        val minutes = (duration / (1000 * 60) % 60).toInt()
        val hours = (duration / (1000 * 60 * 60) % 24).toInt()
        "${timeAddZeros(hours, false)}:${timeAddZeros(minutes)}:${timeAddZeros(seconds)}".apply {
            return if (startsWith(":")) replaceFirst(":", "") else this
        }
    }

    fun getTotalTime(songList: List<Song>): Long {
        var minutes = 0L
        var hours = 0L
        var seconds = 0L
        for (song in songList) {
            seconds += (song.duration / 1000 % 60).toLong()
            minutes += (song.duration / (1000 * 60) % 60).toLong()
            hours += (song.duration / (1000 * 60 * 60) % 24).toLong()
        }
        return hours * (1000 * 60 * 60) + minutes * (1000 * 60) + seconds * 1000
    }

    fun audio2Raw(context: Context, uri: Uri, didTry: Boolean = false): ByteArray? {
        val parcelFileDescriptor = uri.toFileDescriptor(context) ?: return null
        val fis = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val data = try {
            fis.readBytes().optimize()
        } catch (ex: Exception) {
            Timber.e(ex)
            if (didTry) byteArrayOf() else audio2Raw(context, uri, didTry = true)
        }
        fis.close()
        return data
    }

    fun audio2Raw(context: Context, path: String, didTry: Boolean = false): ByteArray? {
        val fis = FileInputStream(File(path))
        val data = try {
            fis.readBytes().optimize()
        } catch (ex: Exception) {
            Timber.e(ex)
            if (didTry) byteArrayOf() else audio2Raw(context, path, didTry = true)
        }
        fis.close()
        return data
    }

    fun toggleShowKeyBoard(context: Context, editText: EditText, show: Boolean) {
        val imm = context.systemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE)
        if (show) {
            editText.apply {
                requestFocus()
                imm.showSoftInput(this, SHOW_IMPLICIT)
            }
        } else {
            editText.apply {
                clearFocus()
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }

    fun addZeros(number: Int?): String {
        if (number!! < 10) return "00${number}"
        if (number < 100) return "0${number}"
        return number.toString()
    }

    private fun timeAddZeros(number: Int?, showIfIsZero: Boolean = true): String {
        return when (number) {
            0 -> if (showIfIsZero) "0${number}" else ""
            1, 2, 3, 4, 5, 6, 7, 8, 9 -> "0${number}"
            else -> number.toString()
        }
    }

    fun getBlackWhiteColor(color: Int): Int {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness >= 0.5) {
            Color.WHITE
        } else Color.BLACK
    }

    fun getStoragePaths(context: Context): List<String> {
        return try {
            val paths: Array<File>? = ContextCompat.getExternalFilesDirs(context, null)
            paths?.map {
                it.path.replace("/Android/data/${context.packageName}/files", "")
            } ?: emptyList()
        } catch (ex: IllegalStateException) {
            emptyList()
        }
    }

    @Suppress("DEPRECATION")
    fun getAlbumArtBitmap(context: Context, albumId: Long): Bitmap {
        try {
            return when {
                SDK_INT >= P -> {
                    val source = ImageDecoder.createSource(
                        context.contentResolver,
                        getAlbumArtUri(albumId)
                    )
                    ImageDecoder.decodeBitmap(source)
                }
                else -> MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    getAlbumArtUri(albumId)
                )
            }
        } catch (e: FileNotFoundException) {
            Timber.e(e)
        } catch (e: UnsupportedOperationException) {
            Timber.e(e)
        }
        return BitmapFactory.decodeResource(context.resources, R.drawable.app_icon)
    }

    fun getExtraBundle(queue: LongArray, title: String): Bundle? {
        return getExtraBundle(queue, title, 0)
    }

    fun getExtraBundle(queue: LongArray, title: String, seekTo: Int?): Bundle? {
        val bundle = Bundle()
        bundle.putLongArray(QUEUE_INFO_KEY, queue)
        bundle.putString(SONG_LIST_NAME, title)
        if (seekTo != null)
            bundle.putInt(SEEK_TO_POS, seekTo)
        else bundle.putInt(SEEK_TO_POS, 0)
        return bundle
    }

    fun isOreo() = SDK_INT >= O
    fun getAlbumArtUri(albumId: Long): Uri = withAppendedId(ARTWORK_URI, albumId)
    fun getSongUri(songId: Long): Uri = withAppendedId(SONG_URI, songId)
}
