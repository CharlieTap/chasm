package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.unwrap
import com.github.michaelbull.result.unwrapError
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `worker fragment relocation matches direct compilation at a nonzero address`() {
        val module = module(definedTypes = listOf(definedType(recursiveType = functionRecursiveType())))
        val store = store()
        val context = CompilerContext(module, ModuleTypeResolver(module), store.heap.registerRuntimeTypes(module.definedTypes))
        val direct = Program().apply { repeat(7) { append(DispatchableInstruction { _, _, next -> next }) } }
        FunctionCompiler(context, protectedFunction, direct).unwrap()
        val fragment = FunctionCompiler(context, protectedFunction, FunctionCompilerWorkspace()).unwrap()
        val installed = Program().apply { repeat(7) { append(DispatchableInstruction { _, _, next -> next }) } }
        assertEquals(7, fragment.program.appendTo(installed))
        val expected = assertNotNull(direct.exceptionTable(7))
        val actual = assertNotNull(installed.exceptionTable(7))
        assertNull(installed.exceptionTable(6))
        assertEquals(expected.endIp, actual.endIp)
        assertEquals(expected.regions.single().startOffset, actual.regions.single().startOffset)
        assertEquals(expected.regions.single().endOffset, actual.regions.single().endOffset)
        assertEquals(expected.regions.single().catches.single().targetOffset, actual.regions.single().catches.single().targetOffset)
    }

    @Test
    fun `failed serial module compilation removes earlier function tables and entries`() {
        val failedFunction = function(idx = Index.FunctionIndex(1u), body = Expression(AtomicMemoryInstruction.Fence))
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(protectedFunction, failedFunction),
        )
        val store = store()
        val types = store.heap.registerRuntimeTypes(module.definedTypes)
        val instance = moduleInstance(runtimeTypes = types, functionAddresses = mutableListOf(functionAddress(0), functionAddress(1)))
        repeat(2) { store.functions.add(wasmFunctionInstance(module = instance)) }
        store.program.append(DispatchableInstruction { _, _, next -> next })
        assertEquals(InstantiationError.UnsupportedThreadsModule, ModuleCompiler(store, module, instance, types).unwrapError())
        assertEquals(1, store.program.size)
        assertFalse(store.program.hasExceptionHandlers)
        for (function in store.functions) {
            assertEquals(-1, (function as io.github.charlietap.chasm.runtime.instance.FunctionInstance.WasmFunction).callStrategy.entryIp)
        }
    }
}
