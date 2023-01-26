package com.example.mappointsapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mappointsapp.databinding.FragmentPointInformationBinding

class PointInformationFragment : Fragment(R.layout.fragment_point_information) {

    private var _binding: FragmentPointInformationBinding? = null
    private val binding: FragmentPointInformationBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPointInformationBinding.bind(view)

        binding.btnMap.setOnClickListener {
            navigateToMap()
        }
    }

    private fun navigateToMap() {
        val direction =
            PointInformationFragmentDirections.actionPointInformationFragmentToMapFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}