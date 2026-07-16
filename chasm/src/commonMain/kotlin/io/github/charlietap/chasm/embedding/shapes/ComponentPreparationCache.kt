package io.github.charlietap.chasm.embedding.shapes

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.config.RuntimeConfig

internal class ComponentPreparationCache<T : Any>() {
    private val entries = mutableListOf<Entry<T>>()

    fun get(config: RuntimeConfig): T? = entries
        .firstOrNull { entry -> entry.matches(config) }
        ?.prepared

    fun put(
        config: RuntimeConfig,
        prepared: T,
    ) {
        entries += Entry(config, prepared)
    }

    private class Entry<T>(
        config: RuntimeConfig,
        val prepared: T,
    ) {
        private val debugInfo = config.debugInfo
        private val bytecodeFusion = config.bytecodeFusion
        private val gcStrategy = config.gcStrategy

        fun matches(config: RuntimeConfig): Boolean =
            debugInfo == config.debugInfo &&
                bytecodeFusion == config.bytecodeFusion &&
                gcStrategy == config.gcStrategy
    }
}

internal inline fun <T : Any, E> cachedPreparation(
    config: RuntimeConfig,
    cache: ComponentPreparationCache<T>,
    crossinline prepare: () -> Result<T, E>,
): Result<T, E> {
    cache.get(config)?.let { prepared -> return Ok(prepared) }

    return prepare().map { prepared ->
        cache.put(config, prepared)
        prepared
    }
}
