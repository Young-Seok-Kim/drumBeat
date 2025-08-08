package com.youngs.drumbeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.navigation.NavigationView
import com.youngs.drumbeat.databinding.FragmentFrameDialogBinding
import com.youngs.drumbeat.data.MenuItem
import com.youngs.drumbeat.ui.drum.DrumFragment
import com.youngs.drumbeat.ui.metronome.MetronomeFragment

//class FrameDialogFragment : DialogFragment() {
//
//    private var _binding: FragmentFrameDialogBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var drawerToggle: ActionBarDrawerToggle
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView: NavigationView
//    private lateinit var toolbar: Toolbar
//    private lateinit var menuItems: List<MenuItem>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 전체 화면 다이얼로그 스타일로 지정
//        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        _binding = FragmentFrameDialogBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        drawerLayout = binding.drawerLayout
//        navigationView = binding.navigationView
//        toolbar = binding.toolbar
//
//        menuItems = arguments?.getParcelableArrayList<MenuItem>("menuItems") ?: emptyList()
//
//        // 액티비티가 AppCompatActivity인지 체크 후 툴바 설정
//        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.apply {
//            setSupportActionBar(toolbar)
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.setHomeButtonEnabled(true)
//        }
//
//        drawerToggle = ActionBarDrawerToggle(
//            requireActivity(),
//            drawerLayout,
//            toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawerLayout.addDrawerListener(drawerToggle)
//        drawerToggle.syncState()
//
//        // 동적으로 NavigationView 메뉴 생성
//        val menu = navigationView.menu
//        menu.clear()
//        menuItems.forEach { item ->
//            menu.add(0, item.id, 0, item.title)
//            // 필요시 .setIcon() 등 추가 가능
//        }
//
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            val selected = menuItems.find { it.id == menuItem.itemId }
//            selected?.let { onMenuItemSelected(it) }
//            drawerLayout.closeDrawers()
//            true
//        }
//
//        // 상단 드럼 프래그먼트를 동적으로 추가 (처음에는 DrumFragment)
//        if (childFragmentManager.findFragmentById(binding.flDrumContainer.id) == null) {
//            childFragmentManager.beginTransaction()
//                .replace(binding.flDrumContainer.id, DrumFragment())
//                .commit()
//        }
//
//        // 하단 메트로놈 프래그먼트 추가 (MetronomeDialogFragment 대신 MetronomeFragment로 대체 필요)
//        if (childFragmentManager.findFragmentById(binding.flMetronomeContainer.id) == null) {
//            childFragmentManager.beginTransaction()
//                .replace(binding.flMetronomeContainer.id, MetronomeFragment())
//                .commit()
//        }
//    }
//
//    private fun onMenuItemSelected(item: MenuItem) {
//        when (item.id) {
//            1 -> {
//                childFragmentManager.beginTransaction()
//                    .replace(binding.flDrumContainer.id, DrumFragment())
//                    .commit()
//            }
//
//            2 -> {
//                // 설정 처리
//            }
//
//            3 -> {
//                // 도움말 처리
//            }
//        }
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
