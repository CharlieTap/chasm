package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.compiler.ParallelTaskExecutor
import io.github.charlietap.chasm.compiler.ParallelTaskScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

internal object CoroutineParallelTaskExecutor : ParallelTaskExecutor {

    override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> = coroutineScope {
        tasks.map { task ->
            async(Dispatchers.Default) {
                val context = coroutineContext
                task(ParallelTaskScope(context::ensureActive))
            }
        }.awaitAll()
    }
}
