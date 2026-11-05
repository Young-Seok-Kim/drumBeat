package com.youngs.beatdrum

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.youngs.beatdrum.data.MenuItem
import com.youngs.beatdrum.databinding.ActivityMainBinding
import com.youngs.beatdrum.ui.main.MainViewModel
import com.youngs.beatdrum.ui.metronome.MetronomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var metronomeFragment: MetronomeFragment

    private var backPressedTime: Long = 0

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        metronomeFragment = supportFragmentManager.findFragmentById(R.id.metronome_fragment) as MetronomeFragment

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateMetronomeVisibility(destination.id)
        }

        // 뒤로가기는 여기 한 곳에서만 처리한다.
        // 드로어 열림 → 닫기, 메인 아님 → 메인으로, 메인 → 두 번 누르면 종료.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return
                }
                if (navController.currentDestination?.id != R.id.mainFragment) {
                    goToMain()
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

        setSupportActionBar(binding.toolbar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationView.itemIconTintList = null // 햄버거 메뉴 아이콘이 회색으로 나와서 해당 코드 추가

        // 1) ViewModel 메뉴 리스트를 관찰하여 NavigationView 메뉴 동적 생성
        mainViewModel.menuItems.observe(this) { items ->
            updateNavigationMenu(items)
        }

        // 2) NavigationView 메뉴 아이템 클릭 리스너 설정 (동적 메뉴 대응)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            mainViewModel.menuItems.value?.find { it.id == menuItem.itemId }?.let { item ->
                mainViewModel.onMenuSelected(item)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // 3) ViewModel 내비게이션 상태 관찰
        mainViewModel.navigateTo.observe(this) { item ->
            item?.let {
                navigateTo(it.fragmentId)
                mainViewModel.onNavigated()
            }
        }
    }

    // 메인은 백스택의 뿌리로 두고, 다른 화면은 메인 위에 하나만 올린다.
    // 전에는 메뉴를 누를 때마다 navigate 로 새 인스턴스를 쌓아 메인→초견→메인→초견이 네 장 쌓였다.
    private fun navigateTo(destinationId: Int) {
        if (destinationId == R.id.mainFragment) {
            goToMain()
            return
        }
        if (navController.currentDestination?.id == destinationId) return
        navController.navigate(destinationId, null, navOptions {
            launchSingleTop = true
            popUpTo(R.id.mainFragment) { inclusive = false }
        })
    }

    private fun goToMain() {
        navController.popBackStack(R.id.mainFragment, false)
    }

    // 메뉴 동적 생성 함수
    private fun updateNavigationMenu(items: List<MenuItem>) {
        val menu = binding.navigationView.menu
        menu.clear()
        items.forEach { item ->
            menu.add(Menu.NONE, item.id, Menu.NONE, getString(item.titleStringId)).setIcon(R.drawable.ic_main)
        }
    }

    private fun updateMetronomeVisibility(destinationId: Int) {
        val menuItem = mainViewModel.menuItems.value?.find { it.fragmentId == destinationId }
        if (menuItem?.showMetronome == true) {
            metronomeFragment.view?.visibility = View.VISIBLE
        } else {
            metronomeFragment.view?.visibility = View.GONE
            metronomeFragment.stopMetronome()
        }
    }

    companion object {
        private const val BACK_PRESS_INTERVAL = 2000L
    }
}
