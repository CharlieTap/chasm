package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.aot.ClassOutput
import io.github.charlietap.chasm.aot.CompileConfig
import io.github.charlietap.chasm.aot.CompileTarget
import io.github.charlietap.chasm.aot.compile
import io.github.charlietap.chasm.aot.module
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import java.util.concurrent.atomic.AtomicInteger

class Aot(
    private val target: CompileTarget = CompileTarget.JVM,
) : Engine {

    override fun name(): String {
        return AOT
    }

    override suspend fun module(
        bytes: ByteArray,
        config: RuntimeConfig,
    ): Module {

        val output = ClassOutput.memory()
        val compileConfig = CompileConfig("$PACKAGE_NAME.Module${modules.incrementAndGet()}", target, config)

        return when (val result = compile(bytes, output, compileConfig)) {
            is ChasmResult.Success -> module(CompiledClassLoader(output).module(result.result.config.className)).expect("the compiled module loads")
            is ChasmResult.Error -> error("Compiling the module ahead of time failed: ${result.error}")
        }
    }

    override fun store(
        module: Module,
        config: RuntimeConfig,
    ): Store {
        return io.github.charlietap.chasm.aot.store(module)
    }

    private companion object {
        const val PACKAGE_NAME = "io.github.charlietap.chasm.integration.compiled"
        val modules = AtomicInteger()
    }
}
