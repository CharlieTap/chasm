package io.github.charlietap.chasm.parallel.coroutines

import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Semaphore

class CallerParticipatingParallelTaskExecutor(
    private val dispatcher: CoroutineDispatcher,
    private val permits: Semaphore,
) : ParallelTaskExecutor {

    override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {

        permits.acquire()
        var helpers = 0
        try {
            while (helpers < tasks.size - 1 && permits.tryAcquire()) helpers++
            return executeLanes(tasks, helpers + 1)
        } finally {
            repeat(helpers + 1) { permits.release() }
        }
    }

    private suspend fun <T> executeLanes(
        tasks: List<ParallelTaskScope.() -> T>,
        lanes: Int,
    ): List<T> {

        val results = arrayOfNulls<Any?>(tasks.size)
        coroutineScope {
            val helpers = (1 until lanes).map { lane ->
                async(dispatcher) {
                    val context = coroutineContext
                    executeLane(tasks, lane, lanes, results, ParallelTaskScope(context::ensureActive))
                }
            }
            val context = coroutineContext
            executeLane(tasks, 0, lanes, results, ParallelTaskScope(context::ensureActive))
            helpers.awaitAll()
        }

        @Suppress("UNCHECKED_CAST")
        return results.asList() as List<T>
    }

    private fun <T> executeLane(
        tasks: List<ParallelTaskScope.() -> T>,
        lane: Int,
        lanes: Int,
        results: Array<Any?>,
        scope: ParallelTaskScope,
    ) {
        for (task in lane until tasks.size step lanes) {
            scope.ensureActive()
            results[task] = tasks[task](scope)
        }
    }
}
