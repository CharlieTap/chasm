@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.aot.CompileConfig
import io.github.charlietap.chasm.aot.CompileOutput
import io.github.charlietap.chasm.aot.CompileResult
import io.github.charlietap.chasm.aot.compile
import io.github.charlietap.chasm.aot.compiledModule
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicInteger

class Aot(
    private val configure: CompileConfig.Builder.() -> Unit = {},
) : Engine {

    override fun name(): String {
        return AOT
    }

    override fun store(config: StoreConfig): Store {

        val store = io.github.charlietap.chasm.aot.store(config)
        storeConfigs[store] = config

        return store
    }

    override suspend fun module(
        bytes: ByteArray,
        store: Store,
        config: RuntimeConfig,
    ): Module {

        val output = CompileOutput.Memory()
        val compileConfig = CompileConfig("$PACKAGE_NAME.module${modules.incrementAndGet()}") {
            store(storeConfig(store, config))
            stacktraces(config.debugInfo)
            configure()
        }

        return when (val result = compile(bytes, output, compileConfig)) {
            is CompileResult.Success -> compiledModule(output.load(result.artifact)).expect("the compiled module loads")
            is CompileResult.Error -> error("Compiling the module ahead of time failed: ${result.error}")
        }
    }

    @Suppress("DEPRECATION")
    private fun storeConfig(
        store: Store,
        config: RuntimeConfig,
    ): StoreConfig {

        val storeConfig = requireNotNull(storeConfigs[store]) { "the store was not made by the aot engine" }

        return StoreConfig(
            meterFuel = storeConfig.meterFuel,
            interruptible = storeConfig.interruptible,
            gcStrategy = storeConfig.gcStrategy ?: config.gcStrategy,
            gcThreshold = storeConfig.gcThreshold ?: config.gcThreshold,
        )
    }

    private companion object {
        const val PACKAGE_NAME = "io.github.charlietap.chasm.integration.compiled"

        // Each compiled module gets its own package, as Transmute gives each module, so frames name one module.
        val modules = AtomicInteger()
        val storeConfigs: MutableMap<Store, StoreConfig> = Collections.synchronizedMap(WeakHashMap())
    }
}
