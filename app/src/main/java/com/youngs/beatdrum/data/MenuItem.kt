package com.youngs.beatdrum.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(val id: Int, val title: String, val titleStringId : Int ) : Parcelable
