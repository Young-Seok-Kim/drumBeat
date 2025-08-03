package com.youngs.drumbeat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.youngs.drumbeat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var intervalSeconds: Int = 3 // 기본 간격
    private val updateRunnable = object : Runnable {
        override fun run() {
            setRandomNumbers()
            // 자신을 intervalSeconds초 후 다시 실행
            handler.postDelayed(this, intervalSeconds * 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRandomNumbers() // 시작시 초기값 세팅

        binding.buttonRandom.setOnClickListener {
            val timeText = binding.editTextTime.text.toString()
            val seconds = timeText.toIntOrNull()

            // 입력 체크 (1~10초)
            if (seconds == null || seconds !in 1..10) {
                Toast.makeText(this, "시간을 1초에서 10초 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            intervalSeconds = seconds

            // 기존 반복 작업 중단
            handler.removeCallbacks(updateRunnable)

            // 즉시 한번 숫자 갱신 (원하면 지워도 됨)
            setRandomNumbers()

            // 반복 작업 시작
            handler.postDelayed(updateRunnable, intervalSeconds * 1000L)

            Toast.makeText(this, "$intervalSeconds 초 마다 숫자가 갱신됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRandomNumbers() {
        binding.number1.text = (0..16).random().toString()
        binding.number2.text = (0..16).random().toString()
        binding.number3.text = (0..16).random().toString()
        binding.number4.text = (0..16).random().toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 액티비티 종료 시 핸들러 콜백 제거
        handler.removeCallbacks(updateRunnable)
    }
}