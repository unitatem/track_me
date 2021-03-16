package com.example.trackme.processing

import com.example.trackme.DeadReckoningActivity
import com.example.trackme.sensor.Vector3
import com.example.trackme.sensor.Vector3f

class DeadReckoningEngine {
    private val TAG: String = DeadReckoningActivity::class.qualifiedName.toString()

    private val mKinematicsEquationX = object: Equation(2) {
        override fun calculateStep(inputs: ArrayList<Float>) {
            val acc = inputs[0]

            dx[0] = x[1]
            dx[1] = acc
        }
    }
    private val mKinematicsEquationY = object: Equation(2) {
        override fun calculateStep(inputs: ArrayList<Float>) {
            val acc = inputs[0]

            dx[0] = x[1]
            dx[1] = acc
        }
    }
    private val mKinematicsEquationZ = object: Equation(2) {
        override fun calculateStep(inputs: ArrayList<Float>) {
            val acc = inputs[0]

            dx[0] = x[1]
            dx[1] = acc
        }
    }

    private val mEquationSolver = Vector3<DifferentialEquationSolver>(
        DifferentialEquationSolver(mKinematicsEquationX),
        DifferentialEquationSolver(mKinematicsEquationY),
        DifferentialEquationSolver(mKinematicsEquationZ)
    )

    fun getPositionX(): Float = mKinematicsEquationX.x[0]
    fun getPositionY(): Float = mKinematicsEquationY.x[0]
    fun getPositionZ(): Float = mKinematicsEquationZ.x[0]

    fun calculateStep(deltaT: Float, acceleration: Vector3f) {
        mEquationSolver.x.solve(deltaT, arrayListOf(acceleration.x))
        mEquationSolver.y.solve(deltaT, arrayListOf(acceleration.y))
        mEquationSolver.z.solve(deltaT, arrayListOf(acceleration.z))
    }
}
