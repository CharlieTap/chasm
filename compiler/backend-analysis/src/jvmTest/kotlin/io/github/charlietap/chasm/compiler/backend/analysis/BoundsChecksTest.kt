package io.github.charlietap.chasm.compiler.backend.analysis

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals

class BoundsChecksTest {

    private val skipped: List<List<Boolean>> = module("bounds").let { module ->
        val declarations = ModuleIndexSpaces(module)
        ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }.functions.map { function ->
            val redundant = function.redundantBoundsChecks(declarations)
            function.instructions.indices.filter { function.instructions[it] is MemoryInstruction }.map { redundant[it] }
        }
    }

    @Test
    fun `a load reaching no further than an earlier one from the same address is not checked again`() {
        assertEquals(listOf(false, true), skipped[0])
    }

    @Test
    fun `a load reaching further than an earlier one is checked`() {
        assertEquals(listOf(false, false), skipped[1])
    }

    @Test
    fun `a load from an address written since the earlier one is checked`() {
        assertEquals(listOf(false, false), skipped[2])
    }

    @Test
    fun `a load control can reach without passing the earlier one is checked`() {
        assertEquals(listOf(false, false), skipped[3])
    }

    @Test
    fun `a load every path to which checked as far is not checked again`() {
        assertEquals(listOf(false, false, true), skipped[4])
    }

    @Test
    fun `a load in a loop covered by one before it is not checked again`() {
        assertEquals(listOf(false, true), skipped[5])
    }

    @Test
    fun `a load from a copy of a checked address is not checked again`() {
        assertEquals(listOf(false, true), skipped[6])
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
