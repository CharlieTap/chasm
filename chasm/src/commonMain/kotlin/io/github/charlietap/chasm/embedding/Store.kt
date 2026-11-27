package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.internal.InterpreterTrapSources
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.parallel.coroutines.DefaultCoroutineParallelTaskExecutor
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(config: RuntimeConfig = RuntimeConfig()): Store {
    return interpreterEngine(
        storeRuntime = StoreRuntime(RuntimeStore(config = config)),
        taskExecutor = DefaultCoroutineParallelTaskExecutor,
        trapSources = InterpreterTrapSources(),
    ).store
}
