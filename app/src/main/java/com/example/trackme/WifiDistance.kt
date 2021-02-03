package com.example.trackme

import kotlin.math.log10
import kotlin.math.pow

class WifiDistance {
    fun getDistance(rssi: Double, frequency: Double): Double {
        return stackoverflowEquation(rssi)
    }

    // TODO
    // https://stackoverflow.com/questions/21850721/best-method-to-determin-distance-with-wi-fi-signal-strength
    // https://journals.sagepub.com/doi/full/10.1155/2014/371350
    private fun freeSpacePathLoss(rssi: Double, frequency: Double): Double {
        // for distance in meters, strength in dB, frequency in MHz
        val k = 27.55
        val frequency_MHz = convertHz2MHz(frequency)

        val d = 10.0.pow((rssi - 20.0 * log10(frequency_MHz) - k) / 20.0)
        return d
    }

    // https://electronics.stackexchange.com/questions/83354/calculate-distance-from-rssi
    private fun stackoverflowEquation(rssi: Double): Double {
        val referenceRssi = -30 // received signal strength in dBm at 1 metre
        val propagationConstant = 2 // also path-loss exponent

        val distance = 10.0.pow((referenceRssi - rssi) / (10.0 * propagationConstant))
        return distance
    }

    companion object {
        fun convertMHz2Hz(mhz: Double): Double {
            return mhz * 1000000.0
        }

        fun convertHz2MHz(hz: Double): Double {
            return hz / 1000000.0
        }
    }
}