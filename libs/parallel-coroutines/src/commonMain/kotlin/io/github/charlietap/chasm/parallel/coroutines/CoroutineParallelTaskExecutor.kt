package io.github.charlietap.chasm.parallel.coroutines

import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

class CoroutineParallelTaskExecutor(
    private val dispatcher: CoroutineDispatcher,
) : ParallelTaskExecutor {

    override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> = coroutineScope {
        tasks.map { task ->
            async(dispatcher) {
                val context = coroutineContext
                task(ParallelTaskScope(context::ensureActive))
            }
        }.awaitAll()
    }
}

val DefaultCoroutineParallelTaskExecutor = CoroutineParallelTaskExecutor(Dispatchers.Default)
