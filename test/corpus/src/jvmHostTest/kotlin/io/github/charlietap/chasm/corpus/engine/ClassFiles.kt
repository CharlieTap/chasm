package io.github.charlietap.chasm.corpus.engine

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.jvm.ClassOutput
import io.github.charlietap.chasm.jvm.CompileConfig
import io.github.charlietap.chasm.jvm.CompileTarget
import io.github.charlietap.chasm.jvm.MemoryClassOutput
import io.github.charlietap.chasm.jvm.compile
import java.util.concurrent.atomic.AtomicInteger
import io.github.charlietap.chasm.compiled.store as compiledStore

class ClassFiles : Engine {

    override fun name(): String {
        return COMPILED
    }

    override suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module {

        val output = ClassOutput.memory()
        val compileConfig = CompileConfig("$PACKAGE_NAME.Module${moduleCount.incrementAndGet()}", compileTarget, config)

        return when (val result = compile(bytes, output, compileConfig)) {
            is ChasmResult.Success -> {
                loadClassFiles(output, result.result.config.className).expect("the compiled module loads")
            }
            is ChasmResult.Error -> {
                error("Compiling the module to class files failed: ${result.error}")
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
        const val PACKAGE_NAME = "io.github.charlietap.chasm.corpus.classfiles"
        val moduleCount = AtomicInteger()
    }
}

internal expect val compileTarget: CompileTarget

internal expect fun loadClassFiles(
    output: MemoryClassOutput,
    className: String,
): ChasmResult<Module, ModuleError>
