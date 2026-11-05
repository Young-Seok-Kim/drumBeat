package com.youngs.beatdrum

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.youngs.beatdrum.databinding.ActivityMainBinding
import com.youngs.beatdrum.ui.metronome.MetronomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var metronomeFragment: MetronomeFragment

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        metronomeFragment = supportFragmentManager.findFragmentById(R.id.metronome_fragment) as MetronomeFragment

        // 하단 탭 ↔ 내비게이션 연결. 탭 id 가 destination id 와 같아서 이 한 줄로 끝난다.
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 메트로놈은 초견 탭에서만. 다른 탭으로 가면 숨기고 멈춘다.
            val show = destination.id == R.id.drumFragment
            metronomeFragment.view?.visibility = if (show) View.VISIBLE else View.GONE
            if (!show) metronomeFragment.stopMetronome()
        }

        // 뒤로가기: 다른 탭이면 초견 탭으로, 초견 탭이면 두 번 눌러 종료.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id != R.id.drumFragment) {
                    binding.bottomNav.selectedItemId = R.id.drumFragment
                    return
                }
                val now = System.currentTimeMillis()
                if (now - backPressedTime < BACK_PRESS_INTERVAL) {
                    finish()
                } else {
                    backPressedTime = now
                    Toast.makeText(this@MainActivity, R.string.press_back_again, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // 앱 공용 배너. BeatDrumApp 에서 시작한 초기화가 끝나면 바로 불린다(이미 끝났으면 즉시).
        MobileAds.initialize(this) {
            if (!isFinishing) binding.adView.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.adView.destroy()
        super.onDestroy()
    }

    companion object {
        private const val BACK_PRESS_INTERVAL = 2000L
    }
}
