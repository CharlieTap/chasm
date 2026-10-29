package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertTrue

class ModuleAnalysisTest {

    @Test
    fun `a caught exception reference is published though nothing declares a reference`() {
        val module = module("caught_reference")
        val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }

        val analysis = analyzeModule(module, code, ModuleIndexSpaces(module))

        assertTrue(analysis.publishesRegisters)
    }

    private fun module(
        name: String,
    ): Module {
        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { it.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
        return WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
    }
}
