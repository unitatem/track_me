package com.example.trackme.sensor

abstract class SensorSubscriber {
    abstract fun update(data: AccelerometerMeasure)
}
