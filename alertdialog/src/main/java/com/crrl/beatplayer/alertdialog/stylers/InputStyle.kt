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

package com.crrl.beatplayer.alertdialog.stylers

import android.graphics.Color
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle

class InputStyle(
    backgroundColor: Int = Color.parseColor("#131313"),
    val inputColor: Int = Color.parseColor("#434343"),
    textColor: Int = Color.parseColor("#FFFFFF"),
    val hintTextColor: Int = Color.parseColor("#AAAAAA"),
    val acceptColor: Int = Color.parseColor("#00FF00"),
    var text: String = "",
    cornerRadius: Float = 60f
) : ItemStyle(backgroundColor, textColor, cornerRadius)