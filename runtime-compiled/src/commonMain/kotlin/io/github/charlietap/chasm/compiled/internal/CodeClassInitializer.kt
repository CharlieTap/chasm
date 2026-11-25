package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.coroutines.CallerParticipatingParallelTaskExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicInteger

internal interface CodeClassInitializer {

    suspend fun initialize(
        loader: ClassLoader,
        classes: List<String>,
    )
}

internal class SharedCodeClassInitializer : CodeClassInitializer {

    override suspend fun initialize(
        loader: ClassLoader,
        classes: List<String>,
    ) {
        shared.initialize(loader, classes)
    }

    private companion object {
        val shared: CodeClassInitializer by lazy {
            ParallelCodeClassInitializer(CallerParticipatingParallelTaskExecutor(Dispatchers.Default, Semaphore(MAXIMUM_WORKERS)))
        }
    }
}

internal class ParallelCodeClassInitializer(
    private val executor: ParallelTaskExecutor,
) : CodeClassInitializer {

    override suspend fun initialize(
        loader: ClassLoader,
        classes: List<String>,
    ) {

        val next = AtomicInteger()
        val worker: ParallelTaskScope.() -> Unit = {
            while (true) {
                val index = next.getAndIncrement()
                if (index >= classes.size) {
                    break
                }
                ensureActive()
                Class.forName(classes[index], true, loader)
            }
        }

        val workers = (classes.size / CLASSES_PER_WORKER).coerceIn(1, MAXIMUM_WORKERS)
        executor.execute(List(workers) { worker })
    }
}

private const val CLASSES_PER_WORKER = 4

private const val MAXIMUM_WORKERS = 4
