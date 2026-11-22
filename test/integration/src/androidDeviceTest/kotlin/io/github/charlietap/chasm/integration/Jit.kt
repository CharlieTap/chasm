package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect

class Jit : Engine {

    override fun name(): String {
        return JIT
    }

    override fun store(config: StoreConfig): Store {
        return io.github.charlietap.chasm.jit.store(config)
    }

    override suspend fun module(
        bytes: ByteArray,
        store: Store,
        config: RuntimeConfig,
    ): Module {
        return module(bytes).expect("the module is valid")
    }
}
