package com.youngs.beatdrum.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(val id: Int, val fragmentId: Int, val title: String, val titleStringId : Int , val showMetronome : Boolean) : Parcelable
