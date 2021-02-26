package com.example.trackme.sensor

abstract class SensorSubscriber {
    abstract fun update(data: Vector3d)
}
