package com.youngs.beatdrum.ui.sightread

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SightreadViewModel : ViewModel() {

    private val _intervalSeconds = MutableLiveData(3)
    val intervalSeconds: LiveData<Int> = _intervalSeconds

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _remainingSeconds = MutableLiveData(3)
    val remainingSeconds: LiveData<Int> = _remainingSeconds

    private val _numbers = MutableLiveData<List<Int>>(listOf(1, 1, 1, 1))
    val numbers: LiveData<List<Int>> = _numbers

    private val _fixScore = MutableLiveData(false) // 악보고정 상태 저장
    val fixScore: LiveData<Boolean> = _fixScore

    private var countDownTimer: CountDownTimer? = null

    fun setInterval(seconds: Int) {
        _intervalSeconds.value = seconds
        _remainingSeconds.value = seconds
    }

    fun setFixScore(fix: Boolean) {
        _fixScore.value = fix
    }

    fun startDrum() {
        if (_isRunning.value == true) return
        _isRunning.value = true
        _numbers.value = List(4) { (1..16).random() }
        _remainingSeconds.value = _intervalSeconds.value ?: 3
        startTimer()
    }

    fun stopDrum() {
        _isRunning.value = false
        countDownTimer?.cancel()
        _remainingSeconds.value = 0
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        val total = _intervalSeconds.value ?: 3
        countDownTimer = object : CountDownTimer((total * 1000).toLong(), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingSeconds.value = ((millisUntilFinished + 999) / 1000).toInt()
            }
            override fun onFinish() {
                if (_isRunning.value != true) return
                // 악보고정이 꺼져 있을 때만 새 악보를 뽑는다. 켜져 있으면 시간만 다시 센다.
                if (_fixScore.value != true) {
                    _numbers.value = List(4) { (1..16).random() }
                }
                _remainingSeconds.value = _intervalSeconds.value
                startTimer()
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
