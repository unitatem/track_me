package com.example.trackme.sensor

abstract class SensorPublisher {
    private val mSubscribers = arrayListOf<SensorSubscriber>()

    fun subscribe(subscriber: SensorSubscriber) {
        mSubscribers.add(subscriber)

        if (mSubscribers.size == 1) {
            firstSubscriberRegistered()
        }
    }

    protected abstract fun firstSubscriberRegistered()

    fun unsubscribe(subscriber: SensorSubscriber) {
        mSubscribers.remove(subscriber)

        if (mSubscribers.isEmpty()) {
            lastSubscriberUnregistered()
        }
    }

    protected abstract fun lastSubscriberUnregistered()

    protected fun notifySubscribers(data: Vector3d) {
        mSubscribers.forEach { it -> it.update(data) }
    }
}
