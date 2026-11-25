package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.corpus.engine.Engine
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store

class CountingEngine(
    private val engine: Engine,
) : Engine {

    var moduleCount = 0
        private set

    var storeCount = 0
        private set

    override fun name(): String {
        return engine.name()
    }

    override suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module {

        moduleCount++

        return engine.module(bytes, config)
    }

    override fun store(
        module: Module,
        config: RuntimeConfig,
    ): Store {

        storeCount++

        return engine.store(module, config)
    }
}
