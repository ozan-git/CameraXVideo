package com.example.android.camerax.video

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.android.camerax.video.databinding.FragmentCaptureStartBinding


class CaptureStartFragment : Fragment() {

    private var _binding: FragmentCaptureStartBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureStartBinding.inflate(inflater, container, false)

        binding.videoButton.setOnClickListener {
            // Navigate to the camera fragment
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                CaptureStartFragmentDirections.actionCaptureStartFragmentToCaptureFragment()
            )
        }

        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}