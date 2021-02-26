package com.example.trackme.processing

import org.junit.Test

import org.junit.Assert.*

class EquationTest {
    private val equation = object: Equation(1) {
        override fun calculate(inputs: ArrayList<Float>) {
            dx[0] = x[0]
        }
    }

    @Test
    fun calculate() {
        // When
        equation.x[0] = 11.1f
        equation.calculate(arrayListOf())

        // Then
        assertEquals(11.1f, equation.dx[0])
    }
}