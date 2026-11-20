package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import java.util.concurrent.atomic.AtomicInteger

internal typealias CodeClassInitializer = suspend (ClassLoader, List<String>, ParallelTaskExecutor) -> Unit

internal suspend fun CodeClassInitializer(
    loader: ClassLoader,
    classes: List<String>,
    executor: ParallelTaskExecutor,
) {
    val next = AtomicInteger()
    val worker: ParallelTaskScope.() -> Unit = {
        while (true) {
            val index = next.getAndIncrement()
            if (index >= classes.size) break
            ensureActive()
            Class.forName(classes[index], true, loader)
        }
    }
    executor.execute(List((classes.size / CLASSES_PER_WORKER).coerceIn(1, MAXIMUM_WORKERS)) { worker })
}

private const val CLASSES_PER_WORKER = 4
private const val MAXIMUM_WORKERS = 4
