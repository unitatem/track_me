package com.example.trackme.processing

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class DifferentialEquationSolverTest {
    private val kinematicsEquation = object: Equation(2) {
        override fun calculateStep(inputs: ArrayList<Float>) {
            val acc = inputs[0]

            dx[0] = x[1]
            dx[1] = acc
        }
    }

    @Test
    fun test_calculatePositionOfObjectMovingWithAcceleration() {
        // When
        val solver = DifferentialEquationSolver(kinematicsEquation)
        for (i in 0 until 1000) {
            solver.solve(0.001f, arrayListOf(10.0f))
        }
        val state = kinematicsEquation.x

        // Then
        Assert.assertEquals(5.0f, state[0], 0.02f)
        Assert.assertEquals(10.0f, state[1], 0.02f)
    }
}
