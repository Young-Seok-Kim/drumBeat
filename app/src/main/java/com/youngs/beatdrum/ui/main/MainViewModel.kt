package com.youngs.beatdrum.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.youngs.beatdrum.R
import com.youngs.beatdrum.data.MenuItem

class MainViewModel : ViewModel() {

    private val _menuItems = MutableLiveData<List<MenuItem>>(listOf(
        MenuItem(1, "메인", R.string.main),
        MenuItem(2, "초견", R.string.stop),
        MenuItem(3, "BPM 측정", R.string.measure_bpm),
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
