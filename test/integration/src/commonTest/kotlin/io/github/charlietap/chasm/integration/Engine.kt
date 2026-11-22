package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store

interface Engine {

    fun name(): String

    fun store(config: StoreConfig = StoreConfig()): Store

    suspend fun module(
        bytes: ByteArray,
        store: Store,
        config: RuntimeConfig,
    ): Module
}

expect val engines: Set<Engine>
