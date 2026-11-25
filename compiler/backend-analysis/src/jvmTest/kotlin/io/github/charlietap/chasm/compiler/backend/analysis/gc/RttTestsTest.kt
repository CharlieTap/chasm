package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_STRUCT
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals

class RttTestsTest {

    private val tests = RttTests(module("storage_plans").definedTypes)

    @Test
    fun `a struct two subtypes below its root sits at depth two`() {
        assertEquals(RttTest(COMPILED_STRUCT, depth = 2, final = false), tests.test(GRANDCHILD))
    }

    @Test
    fun `a struct declared without sub is a final root`() {
        assertEquals(RttTest(COMPILED_STRUCT, depth = 0, final = true), tests.test(SPARE_FILL))
    }

    @Test
    fun `an array is tested against the compiled array class`() {
        assertEquals(RttTest(COMPILED_ARRAY, depth = 0, final = true), tests.test(BYTES))
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val SPARE_FILL = 0
        const val GRANDCHILD = 3
        const val BYTES = 16
    }
}
