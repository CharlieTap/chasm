package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.coroutines.CallerParticipatingParallelTaskExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore

internal val compileExecutor: ParallelTaskExecutor = CallerParticipatingParallelTaskExecutor(
    dispatcher = Dispatchers.Default,
    permits = Semaphore(minOf(MAXIMUM_COMPILE_WORKERS, availableParallelProcessors())),
)

private const val MAXIMUM_COMPILE_WORKERS = 6
