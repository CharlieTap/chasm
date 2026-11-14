package io.github.charlietap.chasm.compiler.backend.analysis.form

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class InstructionFormsTest {

    private val module = module("instruction_table")
    private val code: CirModule = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
    private val declarations = ModuleIndexSpaces(module)

    @Test
    fun `a memory access is decoded once and matches a fresh decode`() {

        val function = function("accesses")
        val forms = InstructionForms(function, declarations)
        val first = assertNotNull(forms.access(1))
        val fresh = assertNotNull((function.instructions[1] as MemoryInstruction).access())

        assertSame(first, forms.access(1))
        assertEquals(fresh.operation, first.operation)
        assertEquals(fresh.address, first.address)
        assertEquals(fresh.value, first.value)
        assertEquals(fresh.offset, first.offset)
        assertEquals(fresh.memory, first.memory)
    }

    @Test
    fun `positions without a form keep answering null`() {

        val forms = InstructionForms(function("accesses"), declarations)

        assertNull(forms.access(0))
        assertNull(forms.access(0))
        assertNull(forms.callSite(1))
        assertNull(forms.callSite(1))
    }

    @Test
    fun `asking for one kind of form does not hide another at the same position`() {

        val function = function("helpers")
        val forms = InstructionForms(function, declarations)
        val position = function.instructions.indexOfFirst { instruction -> instruction.helperCall() != null }

        assertNull(forms.callSite(position))
        assertNull(forms.access(position))
        val call = assertNotNull(forms.helperCall(position))
        assertSame(call, forms.helperCall(position))
    }

    private fun function(name: String): CirFunction {
        val export = module.exports.single { export -> export.name.name == name }
        val index = (export.descriptor as Export.Descriptor.Function).functionIndex.idx.toInt()
        return code.functions.single { function -> function.index.index == index }
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }

        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }
        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }
}
