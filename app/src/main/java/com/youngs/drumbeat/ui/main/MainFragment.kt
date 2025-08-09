package com.youngs.drumbeat.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.youngs.drumbeat.databinding.FragmentMainBinding

class MainFragment : Fragment() {


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private var lastBackPressed: Long = 0
    private val BACK_INTERVAL = 2000L // 2초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val now = System.currentTimeMillis()
                    if (now - lastBackPressed <= BACK_INTERVAL) {
                        isEnabled = false
                        requireActivity().finish()
                    } else {
                        lastBackPressed = now
                        Toast.makeText(requireContext(), "한 번 더 뒤로가기를 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.menuItems.observe(viewLifecycleOwner) { items ->
            binding.buttonContainer.removeAllViews()
            items.forEach { item ->
                if (item.id == 1){
                    return@forEach
                }
                val btn = Button(requireContext()).apply {
                    text = item.title
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = 16
                        marginEnd = 16
                    }
                    setOnClickListener { viewModel.onMenuSelected(item) }
                }
                binding.buttonContainer.addView(btn)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
