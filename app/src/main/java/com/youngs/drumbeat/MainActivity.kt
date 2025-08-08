package com.youngs.drumbeat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.NavigationUI
import androidx.navigation.findNavController
import com.youngs.drumbeat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 인플레이트
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)

        // 네비게이션 컨트롤러 세팅
        val navController = binding.navHostFragment.getFragment<androidx.navigation.fragment.NavHostFragment>().navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        // navController 찾아서 내비게이션 처리
        val navController = binding.navHostFragment.getFragment<androidx.navigation.fragment.NavHostFragment>().navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
