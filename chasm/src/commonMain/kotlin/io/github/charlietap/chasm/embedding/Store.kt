@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.internal.InterpreterEngine
import io.github.charlietap.chasm.embedding.internal.InterpreterModuleInstaller
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(config: StoreConfig = StoreConfig()): Store {
    return interpreterEngine(StoreRuntime(RuntimeStore(config = config)), InterpreterModuleInstaller()).store
}

fun memory(
    store: Store,
    type: MemoryType,
    config: LinearMemoryConfig,
): Memory {
    val engine = store._engine() as? InterpreterEngine
        ?: throw IllegalArgumentException("a memory config only applies to an interpreter store")
    return engine.memory(type, config)
}
