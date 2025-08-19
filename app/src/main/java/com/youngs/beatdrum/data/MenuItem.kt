package com.youngs.beatdrum.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(val id: Int, val fragmentId: Int, val title: String, val titleStringId : Int , val showMetronome : Boolean) : Parcelable

/*
*
* 만약 메뉴를 새로 만들때 새 메뉴의 첫번째 페이지에는 메트로놈이 필요없지만, 두번째 페이지에는 필요한 상황이 생길경우 아래와 같이 구성하여 사용
* data class MenuPage(
    val pageId: Int, // R.id.xxx
    val showMetronome: Boolean
)

data class MenuItem(
    val id: Int, // 메뉴 id
    val title: String,
    val titleStringId: Int,
    val pages: List<MenuPage>
)

* */