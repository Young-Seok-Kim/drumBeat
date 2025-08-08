package com.youngs.drumbeat

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.ui.NavigationUI
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.youngs.drumbeat.data.MenuItem
import com.youngs.drumbeat.databinding.ActivityMainBinding
import com.youngs.drumbeat.ui.drum.DrumFragment
import com.youngs.drumbeat.ui.main.MainFragment
import com.youngs.drumbeat.ui.main.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    // 뷰모델 선언 (ViewModelProvider 등 상황에 맞게)
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            // 클릭된 메뉴 ID와 ViewModel 리스트 매칭
            mainViewModel.menuItems.value?.find { it.id == menuItem.itemId }?.let { item ->
                mainViewModel.onMenuSelected(item)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // 3) ViewModel 내비게이션 상태 관찰하여 프래그먼트 교체 또는 네비게이션 처리
        mainViewModel.navigateTo.observe(this) { item ->
            item?.let {
                when (it.id) {
                    1 -> supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, MainFragment())
                        .commit()
                    2 -> supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, DrumFragment())
                        .commit()
                }
                mainViewModel.onNavigated()
            }
        }

        // 초기 프래그먼트 설정 등 필요 시 추가
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, MainFragment())
                .commit()
        }
    }

    // 메뉴 동적 생성 함수
    private fun updateNavigationMenu(items: List<MenuItem>) {
        val menu = binding.navigationView.menu
        menu.clear()
        items.forEach { item ->
            menu.add(Menu.NONE, item.id, Menu.NONE, item.title)
                .setIcon(R.drawable.test) // 아이콘이 있으면 지정 또는 생략 가능
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 홈 버튼 클릭 시 수행할 동작

                // 예: 네비게이션 컨트롤러를 이용해 메인 프래그먼트로 이동
                val navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
                navController.navigate(R.id.mainFragment)

                // 드로어가 있다면 닫기 처리도 필요하면 추가
                binding.drawerLayout.closeDrawer(GravityCompat.START)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

