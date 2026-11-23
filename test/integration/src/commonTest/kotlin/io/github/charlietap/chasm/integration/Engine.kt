package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store

interface Engine {

    fun name(): String

    suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module

    fun store(
        module: Module,
        config: RuntimeConfig,
    ): Store
}

expect val engines: Set<Engine>
