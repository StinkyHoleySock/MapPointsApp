package com.example.mappointsapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mappointsapp.databinding.FragmentMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map

class MapFragment : Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!
    private lateinit var address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitInitializer.initialize(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)

        val listener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                binding.map.map.mapObjects.clear()
                binding.map.map.mapObjects.addPlacemark(point)
                address = "Широта: ${point.latitude}\nДолгота: ${point.longitude}"
                binding.btnSubmit.visibility = View.VISIBLE
            }
            override fun onMapLongTap(p0: Map, p1: Point) {}
        }

        binding.map.map.addInputListener(listener)
        binding.btnSubmit.setOnClickListener {
            navigateToDetails(address)
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }


    private fun navigateToDetails(address: String?) {
        val direction =
            MapFragmentDirections.actionMapFragmentToPointInformationFragment().setAddress(address)
        findNavController().navigate(direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}