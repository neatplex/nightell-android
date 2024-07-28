package com.neatplex.nightell.data.network

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class RateLimiter(maxRequests: Int, timeWindow: Long, timeUnit: TimeUnit) {
    private val requestSubject = PublishSubject.create<Unit>()
    private val rateLimitedObservable: Observable<Unit>

    init {
        rateLimitedObservable = requestSubject
            .buffer(timeWindow, timeUnit, maxRequests)
            .flatMap { Observable.fromIterable(it) }
    }

    fun acquire() {
        requestSubject.onNext(Unit)
    }

    fun getRateLimitedObservable(): Observable<Unit> {
        return rateLimitedObservable
    }
}
