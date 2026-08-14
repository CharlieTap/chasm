package io.github.charlietap.chasm.coroutines.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

internal class CoroutineParallelTaskExecutor(
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

internal val DefaultCoroutineParallelTaskExecutor = CoroutineParallelTaskExecutor(Dispatchers.Default)

/** Creates a coroutine-backed task executor for official Chasm integrations. */
@InternalChasmApi
fun _coroutineParallelTaskExecutor(
    dispatcher: CoroutineDispatcher,
): ParallelTaskExecutor = CoroutineParallelTaskExecutor(dispatcher)
