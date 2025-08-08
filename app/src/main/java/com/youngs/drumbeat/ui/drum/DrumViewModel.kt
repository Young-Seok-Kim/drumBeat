import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrumViewModel : ViewModel() {

    private val _intervalSeconds = MutableLiveData(3)
    val intervalSeconds: LiveData<Int> = _intervalSeconds

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _remainingSeconds = MutableLiveData(3)
    val remainingSeconds: LiveData<Int> = _remainingSeconds

    private val _numbers = MutableLiveData<List<Int>>(listOf(1,1,1,1))
    val numbers: LiveData<List<Int>> = _numbers

    private var countDownTimer: CountDownTimer? = null

    fun setInterval(seconds: Int) {
        _intervalSeconds.value = seconds
        _remainingSeconds.value = seconds
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
                if (_isRunning.value == true) {
                    _numbers.value = List(4) { (1..16).random() }
                    _remainingSeconds.value = _intervalSeconds.value
                    startTimer()
                }
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
