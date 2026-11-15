package io.github.charlietap.chasm.coroutines.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.coroutines.CoroutineParallelTaskExecutor
import kotlinx.coroutines.CoroutineDispatcher

/** Creates a coroutine-backed task executor for official Chasm integrations. */
@InternalChasmApi
fun _coroutineParallelTaskExecutor(
    dispatcher: CoroutineDispatcher,
): ParallelTaskExecutor = CoroutineParallelTaskExecutor(dispatcher)
