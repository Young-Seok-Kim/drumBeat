package com.youngs.drumbeat

import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.youngs.drumbeat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // 뷰 바인딩 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화 및 setContentView
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 클릭 리스너 설정 (findViewById 대신 binding 사용)
        // MainActivity.kt 예시 (뷰 바인딩 사용 기준)
        binding.buttonOpenFragment.setOnClickListener {
            DrumDialogFragment().show(supportFragmentManager, DrumDialogFragment().javaClass.simpleName)
        }

    }
}

