package com.example.mappointsapp

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mappointsapp.databinding.FragmentMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!
    private lateinit var address: String
    private var userLocationLayer: UserLocationLayer? = null

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


        requestLocationPermission()

        binding.map.map.isRotateGesturesEnabled = false

        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()

        userLocationLayer = mapKit.createUserLocationLayer(binding.map.mapWindow)
        userLocationLayer?.setObjectListener(listener2)

        userLocationLayer?.isVisible = true
        userLocationLayer?.isHeadingEnabled = true

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

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                "android.permission.ACCESS_FINE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf("android.permission.ACCESS_FINE_LOCATION"), 1
            )
        }
    }


    private fun navigateToDetails(address: String?) {
        val direction =
            MapFragmentDirections.actionMapFragmentToPointInformationFragment().setAddress(address)
        findNavController().navigate(direction)
    }

    val listener2 = object : UserLocationObjectListener {
        override fun onObjectAdded(userLocationView: UserLocationView) {
            userLocationLayer!!.setAnchor(
                PointF((binding.map.getWidth() * 0.5) as Float, (binding.map.getHeight() * 0.5) as Float),
                PointF((binding.map.getWidth() * 0.5) as Float, (binding.map.getHeight() * 0.83) as Float)
            )
            userLocationView.arrow.setIcon(
                ImageProvider.fromResource(
                    requireContext(), R.drawable.ic_launcher_background
                )
            )
            val pinIcon = userLocationView.pin.useCompositeIcon()
            pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(requireContext(), R.drawable.ic_launcher_background),
                IconStyle().setAnchor(PointF(0f, 0f))
                    .setRotationType(RotationType.ROTATE)
                    .setZIndex(0f)
                    .setScale(1f)
            )
            pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(requireContext(), R.drawable.ic_launcher_background),
                IconStyle().setAnchor(PointF(0.5f, 0.5f))
                    .setRotationType(RotationType.ROTATE)
                    .setZIndex(1f)
                    .setScale(0.5f)
            )
            userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
        }

        override fun onObjectRemoved(p0: UserLocationView) {
            TODO("Not yet implemented")
        }

        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
            TODO("Not yet implemented")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun onObjectAdded() {

    }
}