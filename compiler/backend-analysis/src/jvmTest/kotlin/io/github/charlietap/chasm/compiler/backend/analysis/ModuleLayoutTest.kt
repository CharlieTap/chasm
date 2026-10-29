package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ModuleLayoutTest {

    @Test
    fun `a module has the same layout hash every time`() {
        assertEquals(ModuleLayout.hash(module("gc")), ModuleLayout.hash(module("gc")))
    }

    @Test
    fun `a global's type is part of the layout`() {
        val module = module("memory")
        val global = module.globals.first()
        val widened = module.copy(globals = listOf(global.copy(type = GlobalType(ValueType.Number(NumberType.F64), global.type.mutability))) + module.globals.drop(1))

        assertNotEquals(ModuleLayout.hash(module), ModuleLayout.hash(widened))
    }

    @Test
    fun `struct layouts are part of the layout`() {
        assertNotEquals(ModuleLayout.hash(module("gc")), ModuleLayout.hash(module("bench_gc")))
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
