package com.example.trackme.sensor

data class Vector3d(var x: Double, var y: Double, var z: Double)

data class Vector3f(var x: Float, var y: Float, var z: Float) {
    constructor(): this(0.0f, 0.0f, 0.0f)
}

data class Vector3<T>(var x: T, var y: T, var z: T) {
//    forEach
}
