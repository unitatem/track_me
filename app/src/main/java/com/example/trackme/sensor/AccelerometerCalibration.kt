package com.example.trackme.sensor

class AccelerometerCalibration(private val mCalibrationSamplesCount: Int) {
    private val mStaticOffset = Vector3f()
    private var mCurrentSamplesCount = 0

    fun needsCalibration(): Boolean {
        return mCurrentSamplesCount < mCalibrationSamplesCount
    }

    fun updateCalibration(measure: Vector3f) {
        val updateAverage: (Float, Int, Float) -> Float = { avg, n, value -> (avg * n + value) / (n + 1) }

        mStaticOffset.x = updateAverage(mStaticOffset.x, mCurrentSamplesCount, measure.x)
        mStaticOffset.y = updateAverage(mStaticOffset.y, mCurrentSamplesCount, measure.y)
        mStaticOffset.z = updateAverage(mStaticOffset.z, mCurrentSamplesCount, measure.z)

        mCurrentSamplesCount++
    }

    fun getStaticOffset(): Vector3f = mStaticOffset
}