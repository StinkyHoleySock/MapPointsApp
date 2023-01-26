package com.example.mappointsapp

import androidx.annotation.Keep
import java.io.Serializable

data class Address(
    @Keep val latitude: Float,
    @Keep val longitude: Float
) : Serializable
