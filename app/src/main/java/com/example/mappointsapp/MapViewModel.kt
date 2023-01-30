package com.example.mappointsapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class MapViewModel : ViewModel() {

    private var _point = MutableLiveData<Point>()
    val point: LiveData<Point> get() = _point

    fun setPoint(point: Point) {
        _point.value = point
    }

    fun resetPoint() {
        _point.value
    }
}