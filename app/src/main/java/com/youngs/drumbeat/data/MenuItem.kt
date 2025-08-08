package com.youngs.drumbeat.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(val id: Int, val title: String) : Parcelable
