package io.github.charlietap.chasm.corpus.engine

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store as interpreterStore

class Interpreter : Engine {

    override fun name(): String {
        return INTERPRETER
    }

    override suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module {
        return module(bytes).expect("the module is valid")
    }

    override fun store(
        module: Module,
        config: RuntimeConfig,
    ): Store {
        return interpreterStore(config)
    }
}
