package com.example.mappointsapp

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mappointsapp.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment(R.layout.fragment_map), UserLocationObjectListener, CameraListener {

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!
    private lateinit var address: String
    private val viewModel: MapViewModel by viewModels()
    private lateinit var userLocationLayer: UserLocationLayer
    private var routeStartLocation = Point(0.0, 0.0)
    private var permissionLocation = false
    private var followUserLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitInitializer.initialize(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        binding.map.map.isRotateGesturesEnabled = false

        setPoint()
        subscribeUI()
        checkPermission()
        setUserLocation()
    }

    private fun setPoint() {
        val listener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                viewModel.setPoint(point)
            }

            override fun onMapLongTap(p0: Map, p1: Point) {}
        }
        binding.map.map.addInputListener(listener)
    }

    private fun subscribeUI() {
        viewModel.point.observe(viewLifecycleOwner) { point ->
            binding.map.map.mapObjects.clear()
            binding.map.map.mapObjects.addPlacemark(point)
            address = "Широта: ${point.latitude}\nДолгота: ${point.longitude}"
            binding.btnSubmit.visibility = View.VISIBLE
        }
        binding.btnSubmit.setOnClickListener {
            navigateToDetails(address)
        }
    }

    private fun checkPermission() {
        val permissionLocation =
            checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            onMapReady()
        }
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.map.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)
        binding.map.map.addCameraListener(this)
        cameraUserPosition()
        permissionLocation = true

    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            routeStartLocation = userLocationLayer.cameraPosition()!!.target
            binding.map.map.move(
                CameraPosition(routeStartLocation, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            binding.map.map.move(CameraPosition(Point(60.07, 30.034), 16f, 0f, 0f))
        }
    }



    private fun setUserLocation() {
        val mapLogoAlignment = Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
        binding.map.map.logo.setAlignment(mapLogoAlignment)
        if (permissionLocation) {
            cameraUserPosition()
        } else {
            checkPermission()
        }
    }

    private fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF((binding.map.width * 0.5).toFloat(), (binding.map.height * 0.5).toFloat()),
            PointF((binding.map.width * 0.5).toFloat(), (binding.map.height * 0.83).toFloat())
        )
    }

    private fun noAnchor() {
        userLocationLayer.resetAnchor()
    }

    override fun onStop() {
        binding.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.map.onStart()
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

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                requireContext(), R.drawable.ic_empty
            )
        )

        val pinIcon = userLocationView.pin.useCompositeIcon()

        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(requireContext(), R.drawable.ic_empty),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )

        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(requireContext(), R.drawable.ic_empty),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )

        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT


        userLocationView.arrow.isVisible = false
        userLocationView.pin.isVisible = false
        userLocationView.accuracyCircle.isVisible = false
    }

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        finish: Boolean
    ) {
        if (finish) {
            if (followUserLocation) {
                setAnchor()
            }
        } else {
            if (!followUserLocation) {
                noAnchor()
            }
        }
    }
}