package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstantExpressionsTest {

    private val expressions = ConstantExpressions(module("constant_expressions"))

    @Test
    fun `a global initialised with struct new needs code`() {
        assertTrue(BOXED in expressions.globals)
        assertTrue(expressions.needed)
    }

    @Test
    fun `a global read of an imported reference global needs code`() {
        assertTrue(COPIED in expressions.globals)
    }

    @Test
    fun `a global read of an imported numeric global needs none`() {
        assertFalse(COUNTED in expressions.globals)
        assertFalse(PLAIN in expressions.globals)
    }

    @Test
    fun `a table initialised with struct new needs code`() {
        assertEquals(listOf(BOXES), expressions.tables)
    }

    @Test
    fun `a ref func element segment needs none`() {
        assertFalse(FUNCTIONS in expressions.elements)
    }

    @Test
    fun `an element segment holding struct new needs code`() {
        assertEquals(listOf(BOXED_ITEMS), expressions.elements)
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val PLAIN = 0
        const val BOXED = 1
        const val COPIED = 2
        const val COUNTED = 3
        const val BOXES = 1
        const val FUNCTIONS = 0
        const val BOXED_ITEMS = 1
    }
}
