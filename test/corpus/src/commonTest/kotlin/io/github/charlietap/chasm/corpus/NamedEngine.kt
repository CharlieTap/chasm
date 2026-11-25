package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.corpus.engine.Engine
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store

class NamedEngine(
    private val name: String,
    private val engine: Engine,
) : Engine {

    override fun name(): String {
        return name
    }

    override suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module {
        return engine.module(bytes, config)
    }

    override fun store(
        module: Module,
        config: RuntimeConfig,
    ): Store {
        return engine.store(module, config)
    }
}
