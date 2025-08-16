package com.youngs.beatdrum.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.youngs.beatdrum.R
import com.youngs.beatdrum.data.MenuItem

class MainViewModel : ViewModel() {

    private val _menuItems = MutableLiveData<List<MenuItem>>(listOf(
        MenuItem(1, R.id.mainFragment, "메인", R.string.main, false),
        MenuItem(2, R.id.drumFragment, "초견", R.string.sightread, true),
        MenuItem(3, R.id.selfBeatFragment, "BPM 측정", R.string.measure_bpm, false),
    ))
    val menuItems: LiveData<List<MenuItem>> = _menuItems

    private val _navigateTo = MutableLiveData<MenuItem?>()
    val navigateTo: LiveData<MenuItem?> = _navigateTo

    fun onMenuSelected(item: MenuItem) {
        _navigateTo.value = item
    }

    fun onNavigated() {
        _navigateTo.value = null
    }
}
