package com.example.mappointsapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mappointsapp.databinding.FragmentMapBinding

class MapFragment: Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)

    }

    private fun navigateToMap(address: Address) {
        val direction =
            MapFragmentDirections.actionMapFragmentToPointInformationFragment(address)
        findNavController().navigate(direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}