package io.github.charlietap.chasm.parallel

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
fun interface ParallelTaskScope {

    fun ensureActive()
}

@InternalChasmApi
interface ParallelTaskExecutor {

    /** Executes [tasks] and returns their results in the same order. */
    suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T>
}

/** Runs [worker] [count] times in parallel. A single worker runs on the calling thread. */
@InternalChasmApi
suspend fun <T> ParallelTaskExecutor.executeWorkers(
    count: Int,
    worker: ParallelTaskScope.() -> T,
): List<T> = if (count == 1) listOf(UncancellableTaskScope.worker()) else execute(List(count) { worker })

private val UncancellableTaskScope = ParallelTaskScope {}
