package com.youngs.drumbeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.youngs.drumbeat.databinding.FragmentFrameDialogBinding

class FrameDialogFragment : DialogFragment() {

    private var _binding: FragmentFrameDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 전체 화면 다이얼로그 스타일로 지정
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFrameDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단 드럼 프래그먼트를 동적으로 추가 (처음에는 DrumFragment)
        if (childFragmentManager.findFragmentById(binding.flDrumContainer.id) == null) {
            childFragmentManager.beginTransaction()
                .replace(binding.flDrumContainer.id, DrumFragment())
                .commit()
        }

        // 하단 메트로놈 프래그먼트 추가 (MetronomeDialogFragment 대신 MetronomeFragment로 대체 필요)
        if (childFragmentManager.findFragmentById(binding.flMetronomeContainer.id) == null) {
            childFragmentManager.beginTransaction()
                .replace(binding.flMetronomeContainer.id, MetronomeFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
