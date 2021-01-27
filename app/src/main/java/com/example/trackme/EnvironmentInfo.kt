package com.example.trackme

import android.os.Build

class EnvironmentInfo {
    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic")
    }

    fun isRealDevice(): Boolean {
        return !isEmulator()
    }
}
