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
    private var intervalSeconds: Int = 3

    // 반복 실행되는 Runnable 객체
    private val updateRunnable = object : Runnable {
        override fun run() {
            setRandomNumbersAndImages()
            handler.postDelayed(this, intervalSeconds * 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 시작 시 초기 값 세팅
        setRandomNumbersAndImages()

        binding.buttonRandom.setOnClickListener {
            val timeText = binding.editTextTime.text.toString()
            val seconds = timeText.toIntOrNull()

            if (seconds == null || seconds !in 1..10) {
                Toast.makeText(this, "시간을 1초에서 10초 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            intervalSeconds = seconds

            // 기존 반복 작업 제거
            handler.removeCallbacks(updateRunnable)

            // 즉시 한 번 실행
            setRandomNumbersAndImages()

            // 반복 작업 시작
            handler.postDelayed(updateRunnable, intervalSeconds * 1000L)

            Toast.makeText(this, "$intervalSeconds 초마다 숫자와 이미지가 갱신됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRandomNumbersAndImages() {
        val numbers = List(4) { (1..16).random() }

        val numberTextViews = listOf(binding.number1, binding.number2, binding.number3, binding.number4)
        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)

        for (i in numbers.indices) {
            val num = numbers[i]
            numberTextViews[i].text = num.toString()

            if (num in 1..16) {
                val imageResId = resources.getIdentifier("beat$num", "drawable", packageName)
                if (imageResId != 0) {
                    imageViews[i].setImageResource(imageResId)
                } else {
                    imageViews[i].setImageDrawable(null)
                }
            } else {
                // 숫자가 0이면 이미지뷰 비움
                imageViews[i].setImageDrawable(null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable) // 메모리 누수 방지
    }
}