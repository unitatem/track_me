package com.example.trackme.processing

import org.junit.Test

import org.junit.Assert.*

class EquationTest {
    private val equation = object: Equation(1) {
        override fun calculateStep(inputs: ArrayList<Float>) {
            dx[0] = x[0]
        }
    }

    @Test
    fun equationStateUpdatedAfterStepCalculation() {
        // When
        equation.x[0] = 11.1f
        equation.calculateStep(arrayListOf())

        // Then
        assertEquals(11.1f, equation.dx[0])
    }
}