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

    private val _numbers = MutableLiveData<List<Int>>(listOf(1,1,1,1))
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
                if (_isRunning.value == true) {
                    if (_fixScore.value == true) {
                        // 악보고정이 켜져 있으면 이미지 변경하지 않고, 남은 시간을 리셋만 함
                        _remainingSeconds.value = _intervalSeconds.value
                        startTimer() // 타이머 재시작
                    } else {
                        // 악보고정을 껐을 때 즉, 체크 해제 시에만 이미지 바뀌도록 수정
                        _numbers.value = List(4) { (1..16).random() }
                        _remainingSeconds.value = _intervalSeconds.value
                        startTimer()
                    }
                }
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}

