package com.example.trackme.processing

abstract class Equation(val mSolutionSpaceSize: Int) {
    var x = ArrayList<Float>()
    var dx = ArrayList<Float>()

    init {
        for (i in 0 until mSolutionSpaceSize) {
            x.add(0.0f)
            dx.add(0.0f)
        }
    }

    abstract fun calculateStep(inputs: ArrayList<Float>)
}

// TODO
// https://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods
// https://www.meil.pw.edu.pl/za/ZA/Courses/Informatyka-2/Wyklady
class DifferentialEquationSolver(private val mEquation: Equation) {
    fun solve(deltaT: Float, inputs: ArrayList<Float>) {
        trapezoidalMethod(deltaT, inputs)
    }

    private fun trapezoidalMethod(deltaT: Float, inputs: ArrayList<Float>) {
        // store copy
        val previousDx = ArrayList<Float>()
        for (i in 0 until mEquation.mSolutionSpaceSize) {
            previousDx.add(mEquation.dx[i])
        }

        mEquation.calculateStep(inputs)

        for (i in 0 until mEquation.mSolutionSpaceSize) {
            mEquation.x[i] += 0.5f * (previousDx[i] + mEquation.dx[i]) * deltaT
        }
    }
}
