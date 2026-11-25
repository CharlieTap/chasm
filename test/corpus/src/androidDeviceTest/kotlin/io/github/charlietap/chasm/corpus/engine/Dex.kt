package io.github.charlietap.chasm.corpus.engine

import io.github.charlietap.chasm.compiled.DexInput
import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.dex.CompileConfig
import io.github.charlietap.chasm.dex.DexOutput
import io.github.charlietap.chasm.dex.compile
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import java.util.concurrent.atomic.AtomicInteger
import io.github.charlietap.chasm.compiled.store as compiledStore

class Dex : Engine {

    override fun name(): String {
        return COMPILED
    }

    override suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module {

        val output = DexOutput.memory()
        val compileConfig = CompileConfig("$PACKAGE_NAME.Module${moduleCount.incrementAndGet()}", config)

        return when (val result = compile(bytes, output, compileConfig)) {
            is ChasmResult.Success -> {
                val input = DexInput.memory(output.dexFiles, output.resources)
                module(input, result.result.config.className).expect("the compiled module loads")
            }
            is ChasmResult.Error -> {
                error("Compiling the module to DEX failed: ${result.error}")
            }
        }
    }

    override fun store(
        module: Module,
        config: RuntimeConfig,
    ): Store {
        return compiledStore(module)
    }

    private companion object {
        const val PACKAGE_NAME = "io.github.charlietap.chasm.corpus.dex"
        val moduleCount = AtomicInteger()
    }
}
