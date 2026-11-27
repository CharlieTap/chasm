package io.github.charlietap.chasm.parallel

import io.github.charlietap.chasm.InternalChasmApi

/** How much work justifies sharing it between threads, in whatever unit the caller measures work in. */
@InternalChasmApi
class ParallelismPolicy(
    val minimumWork: Long,
    val workPerWorker: Long,
    val minimumWorkers: Int = 2,
    val maximumWorkers: Int = Int.MAX_VALUE,
) {
    init {
        require(workPerWorker > 0) { "workPerWorker must be positive" }
    }
}
