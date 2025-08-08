package com.youngs.drumbeat.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.youngs.drumbeat.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.menuItems.observe(viewLifecycleOwner) { items ->
            binding.buttonContainer.removeAllViews()
            items.forEach { item ->
                val btn = Button(requireContext()).apply {
                    text = item.title
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        marginStart = 16
                        marginEnd = 16
                    }
                    setOnClickListener {
                        viewModel.onMenuSelected(item)
                    }
                }
                binding.buttonContainer.addView(btn)
            }
        }

        viewModel.navigateTo.observe(viewLifecycleOwner) { item ->
            item?.let {
                when (it.id) {
                    1 -> findNavController().navigate(MainFragmentDirections.actionMainToDrumFragment())
//                    2 -> findNavController().navigate(MainFragmentDirections.actionMainToSettingsFragment())
//                    3 -> findNavController().navigate(MainFragmentDirections.actionMainToHelpFragment())
                }
                viewModel.onNavigated()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}