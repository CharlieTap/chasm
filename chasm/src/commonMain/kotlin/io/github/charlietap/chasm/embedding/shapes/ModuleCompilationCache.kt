package io.github.charlietap.chasm.embedding.shapes

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.config.RuntimeConfig

internal class ModuleCompilationCache<T : Any>(
    config: RuntimeConfig,
    compiled: T,
) {
    private val entries = mutableListOf(Entry(config, compiled))

    fun get(config: RuntimeConfig): T? = entries
        .firstOrNull { entry -> entry.matches(config) }
        ?.compiled

    fun put(
        config: RuntimeConfig,
        compiled: T,
    ) {
        entries += Entry(config, compiled)
    }

    private class Entry<T>(
        config: RuntimeConfig,
        val compiled: T,
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

internal inline fun <T : Any, E> cachedCompilation(
    config: RuntimeConfig,
    cache: ModuleCompilationCache<T>?,
    crossinline compiler: () -> Result<T, E>,
    crossinline cacheCompiled: (ModuleCompilationCache<T>) -> Unit,
): Result<T, E> {
    cache?.get(config)?.let { compiled -> return Ok(compiled) }

    return compiler().map { compiled ->
        if (cache == null) {
            cacheCompiled(ModuleCompilationCache(config, compiled))
        } else {
            cache.put(config, compiled)
        }
        compiled
    }
}
