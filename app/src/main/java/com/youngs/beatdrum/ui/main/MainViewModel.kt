package com.youngs.beatdrum.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.youngs.beatdrum.data.MenuItem

class MainViewModel : ViewModel() {

    private val _menuItems = MutableLiveData<List<MenuItem>>(listOf(
        MenuItem(1, "메인"),
        MenuItem(2, "드럼"),
        MenuItem(3, "BPM 측정"),
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
