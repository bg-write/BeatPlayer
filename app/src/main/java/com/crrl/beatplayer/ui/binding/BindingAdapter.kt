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

package com.crrl.beatplayer.ui.binding

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v4.media.session.PlaybackStateCompat.*
import android.text.Html
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.models.SearchData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.widgets.MusicVisualizer
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_TYPE
import com.crrl.beatplayer.utils.BeatConstants.ARTIST_TYPE
import com.crrl.beatplayer.utils.BeatConstants.FAVORITE_TYPE
import com.crrl.beatplayer.utils.BeatConstants.FOLDER_TYPE
import com.crrl.beatplayer.utils.GeneralUtils.PORTRAIT
import com.crrl.beatplayer.utils.GeneralUtils.getAlbumArtUri
import com.crrl.beatplayer.utils.GeneralUtils.getOrientation
import com.github.florent37.kotlin.pleaseanimate.please
import rm.com.audiowave.AudioWaveView
import timber.log.Timber

/**
 * @param view is the target view.
 * @param albumId is the id that will be used to get the image form the DB.
 * @param recycled, if it is true the placeholder will be the last song cover selected.
 * */
@BindingAdapter("app:albumId", "app:recycled", requireAll = false)
fun setAlbumId(
    view: ImageView,
    albumId: Long,
    recycled: Boolean = false
) {
    view.clipToOutline = true

    val drawable = getDrawable(view.context, R.drawable.ic_empty_cover)
    Glide.with(view)
        .load(getAlbumArtUri(albumId))
        .transition(withCrossFade()).apply {
            if (recycled) {
                error(Glide.with(view).load(drawable))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            view.setImageDrawable(placeholder)
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            view.setImageDrawable(resource)
                        }
                    })
            } else {
                placeholder(R.drawable.ic_empty_cover)
                    .error(R.drawable.ic_empty_cover)
                    .into(view)
            }
        }
}

@BindingAdapter("app:width", "app:height", requireAll = false)
fun setImageSize(view: View, width: Int? = null, height: Int? = null) {
    view.layoutParams.apply {
        this.width = width ?: this.width
        this.height = height ?: this.height
    }
    if (view is ImageView) view.scaleType = ImageView.ScaleType.CENTER_CROP
}

@BindingAdapter("app:html")
fun setTextHtml(view: TextView, html: String) {
    view.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
}

@BindingAdapter("app:track_number")
fun setTrackNumber(view: TextView, trackNumber: Int) {
    val numberStr = when (trackNumber) {
        0 -> "-"
        else -> trackNumber.toString()
    }

    view.text = numberStr
}

@BindingAdapter("app:isFav")
fun isSongFav(view: ImageButton, isFav: Boolean) {
    if (isFav) {
        view.setImageDrawable(getDrawable(view.context, R.drawable.ic_favorite))
    } else {
        view.setImageDrawable(getDrawable(view.context, R.drawable.ic_no_favorite))
    }
}

@BindingAdapter("app:playState")
fun setPlayState(view: ImageView, state: Int) {
    when (state) {
        STATE_PLAYING -> view.setImageResource(R.drawable.ic_pause)
        else -> view.setImageResource(R.drawable.ic_play)
    }
}

@BindingAdapter("app:repeatState")
fun setRepeatState(view: ImageView, state: Int) {
    when (state) {
        REPEAT_MODE_ONE -> view.apply { setImageResource(R.drawable.ic_repeat_one) }
        REPEAT_MODE_ALL -> view.setImageResource(R.drawable.ic_repeat_all)
        else -> view.setImageResource(R.drawable.ic_repeat)
    }
}

@BindingAdapter("app:shuffleState")
fun setShuffleState(view: ImageView, state: Int) {
    when (state) {
        SHUFFLE_MODE_ALL -> view.setImageResource(R.drawable.ic_shuffle_all)
        else -> view.setImageResource(R.drawable.ic_shuffle)
    }
}

@BindingAdapter("app:raw")
fun updateRawData(view: AudioWaveView, raw: ByteArray) {
    try {
        view.setRawData(raw)
    } catch (e: IllegalStateException) {
        Timber.e(e)
    }
}

@BindingAdapter("app:selectedSongs")
fun setTextTitle(view: TextView, selectedSongs: MutableList<Song>) {
    if (selectedSongs.size == 0) {
        view.setText(R.string.select_tracks)
    } else {
        view.text = "${selectedSongs.size}"
    }
}

@BindingAdapter("app:type")
fun setTextByType(view: TextView, type: String) {
    view.apply {
        text = when (type) {
            ARTIST_TYPE -> context.getString(R.string.artist)
            ALBUM_TYPE -> context.getString(R.string.albums)
            FOLDER_TYPE -> context.getString(R.string.folders)
            else -> {
                view.visibility = GONE
                ""
            }
        }
    }
}

@BindingAdapter("app:title", "app:detail", requireAll = false)
fun setTextTitle(view: TextView, favorite: Favorite, detail: Boolean = false) {
    view.apply {
        text = if (favorite.type == FAVORITE_TYPE) {
            context.getString(R.string.favorite_music)
        } else {
            favorite.title
        }
    }
}

@BindingAdapter("app:type", "app:count")
fun setCount(view: TextView, type: String, count: Int) {
    view.text = view.resources.getQuantityString(
        if (type == ARTIST_TYPE) {
            R.plurals.number_of_albums
        } else {
            R.plurals.number_of_songs
        },
        count,
        count
    )
}

@BindingAdapter("app:by", "app:data")
fun setTextCount(view: TextView, type: String, data: SearchData) {
    val count = when (type) {
        ARTIST_TYPE -> data.artistList.size
        ALBUM_TYPE -> data.albumList.size
        else -> data.songList.size
    }
    val id = when (type) {
        ARTIST_TYPE -> R.plurals.number_of_artists
        ALBUM_TYPE -> R.plurals.number_of_albums
        else -> R.plurals.number_of_songs
    }
    view.text = view.resources.getQuantityString(id, count, count)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:album")
fun fixArtistLength(view: TextView, album: Album) {
    val maxSize = if (getOrientation(view.context) == PORTRAIT) 13 else 8
    album.apply {
        view.text = "${
            if (artist.length > maxSize) {
                artist.substring(0, maxSize)
            } else {
                artist
            }
        } ${view.resources.getString(R.string.separator)} ${
            view.resources.getQuantityString(
                R.plurals.number_of_songs,
                songCount,
                songCount
            )
        }"
    }
}

@BindingAdapter("app:clipToOutline")
fun setClipToOutline(view: View, clipToOutline: Boolean) {
    view.clipToOutline = clipToOutline
}

@BindingAdapter("app:textUnderline")
fun textUnderline(view: TextView, textUnderline: Boolean) {
    if (textUnderline)
        view.text = Html.fromHtml("<u>${view.text}</u>", Html.FROM_HTML_MODE_LEGACY)
}

@BindingAdapter("app:type")
fun setMarginByType(view: View, type: String) {
    val padding = view.resources.getDimensionPixelSize(R.dimen.padding_12)
    when (type) {
        ARTIST_TYPE, ALBUM_TYPE -> view.setPaddings(top = padding, right = padding)
    }
}

@BindingAdapter("app:visible", "app:animate", requireAll = false)
fun setVisibility(view: View, visible: Boolean = true, animate: Boolean = false) {
    view.toggleShow(visible, animate)
}

@BindingAdapter("app:hide")
fun setHide(view: View, hide: Boolean = true) {
    if(hide) view.visibility = View.INVISIBLE
    else view.visibility = VISIBLE
}

@BindingAdapter("app:selected", "app:marquee")
fun setSelectedTextColor(view: TextView, selected: Boolean, marquee: Boolean) {
    please(200) {
        animate(view) {
            if (selected) {
                val color = view.context.getColorByTheme(R.attr.colorAccent)
                textColor(color)
                view.setCustomColor(color, opacity = !marquee)
                view.isSelected = marquee
            } else {
                val color = view.context.getColorByTheme(
                    if (marquee) R.attr.titleTextColor
                    else R.attr.subTitleTextColor
                )
                view.setCustomColor(color)
            }
        }
    }
}

@BindingAdapter("app:show", "app:state")
fun setVisualizerVisibility(view: LinearLayout, visible: Boolean, state: Int) {
    if (visible) {
        if (state == STATE_PLAYING) view.show(false) else view.hide(false)
    } else view.hide(false)
}

@BindingAdapter("app:slide", "app:state")
fun setContainer(view: RelativeLayout, visible: Boolean, state: Int) {
    if (visible) {
        if (state == STATE_PLAYING) {
            view.slideRight(true)
        } else {
            view.slideLeft(true)
        }
    } else view.slideLeft(false)
}

@BindingAdapter("app:state")
fun setScale(view: View, state: Int) {
    if (state == STATE_PLAYING) {
        view.scaleUp()
    } else {
        view.scaleDown()
    }
}

@BindingAdapter("app:album", "app:artist")
fun setArtistAlbum(view: TextView, artist: String?, album: String?) {
    view.text = if (artist != null && album != null)
        view.context.getString(R.string.with_separator, artist, album)
    else artist ?: album ?: ""
}

@BindingAdapter("app:isSelected", requireAll = true)
fun setSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}
