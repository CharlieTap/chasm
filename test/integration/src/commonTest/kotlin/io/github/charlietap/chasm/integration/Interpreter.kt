package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect

class Interpreter : Engine {

    override fun name(): String {
        return INTERPRETER
    }

    override fun store(config: StoreConfig): Store {
        return io.github.charlietap.chasm.embedding.store(config)
    }

    override suspend fun module(
        bytes: ByteArray,
        store: Store,
        config: RuntimeConfig,
    ): Module {
        return module(bytes).expect("the module is valid")
    }
}
