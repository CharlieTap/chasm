package io.github.charlietap.chasm.compiler.backend.chasm

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ExceptionPublicationTest {
    private val protectedFunction = function(
        body = Expression(
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.TryTable(BlockType.Empty, listOf(ControlInstruction.CatchHandler.CatchAll(Index.LabelIndex(0u)))),
            ControlInstruction.Unreachable,
            ControlInstruction.End(2),
        ),
    )

    @Test
    fun `linking at a nonzero address publishes the same function relative table`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(protectedFunction),
        )
        val direct = linkFunction(module).program
        val installed = Program().apply { repeat(7) { append(DispatchableInstruction { _, _, next -> next }) } }
        assertEquals(7, linkFunction(module, program = installed).entryIp)
        val expected = assertNotNull(direct.exceptionTable(0))
        val actual = assertNotNull(installed.exceptionTable(7))
        assertNull(installed.exceptionTable(6))
        assertEquals(expected.endIp + 7, actual.endIp)
        assertEquals(expected.regions.single().startOffset, actual.regions.single().startOffset)
        assertEquals(expected.regions.single().endOffset, actual.regions.single().endOffset)
        assertEquals(expected.regions.single().catches.single().targetOffset, actual.regions.single().catches.single().targetOffset)
    }

    @Test
    fun `failed module linking removes earlier function tables and entries`() {
        val failedFunction = function(idx = Index.FunctionIndex(1u), body = Expression(ControlInstruction.Call(Index.FunctionIndex(2u))))
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(protectedFunction, failedFunction, function(idx = Index.FunctionIndex(2u))),
        )
        val compiled = ModuleCompiler(module).unwrap()
        val linked = CirModule(functions = compiled.functions.take(2))
        val store = store()
        val types = store.heap.registerRuntimeTypes(module.definedTypes)
        val instance = moduleInstance(runtimeTypes = types, functionAddresses = mutableListOf(functionAddress(0), functionAddress(1)))
        repeat(2) { store.functions.add(wasmFunctionInstance(module = instance)) }
        store.program.append(DispatchableInstruction { _, _, next -> next })
        val entryIps = store.functions.map { function -> (function as FunctionInstance.WasmFunction).callStrategy.entryIp }

        assertFailsWith<IndexOutOfBoundsException> { ModuleLinker(store, instance, module, linked) }

        assertEquals(1, store.program.size)
        assertFalse(store.program.hasExceptionHandlers)
        assertEquals(entryIps, store.functions.map { function -> (function as FunctionInstance.WasmFunction).callStrategy.entryIp })
    }
}
