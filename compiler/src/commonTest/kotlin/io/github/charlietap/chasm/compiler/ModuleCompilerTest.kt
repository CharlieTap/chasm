package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.unwrapError
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ModuleCompilerTest {

    @Test
    fun `failed compilation removes new code and entries while preserving installed functions`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(
                function(body = Expression(ControlInstruction.Nop)),
                function(idx = Index.FunctionIndex(1u), body = Expression(AtomicMemoryInstruction.Fence)),
            ),
        )
        val store = store()
        val existingInstruction = dispatchableInstruction()
        val existingTable = FunctionExceptionTable(0, 1, 0, 0, emptyArray(), intArrayOf())
        val existingFunction = wasmFunctionInstance().apply { callStrategy.entryIp = 0 }
        store.program.append(existingInstruction)
        store.program.registerExceptionTable(existingTable)
        store.functions.add(existingFunction)

        val types = store.heap.registerRuntimeTypes(module.definedTypes)
        val instance = moduleInstance(
            runtimeTypes = types,
            functionAddresses = mutableListOf(functionAddress(1), functionAddress(2)),
        )
        val functions = List(2) {
            wasmFunctionInstance(module = instance).apply { callStrategy.entryIp = -1 }
        }
        store.functions.addAll(functions)

        val result = ModuleCompiler(store, module, instance, types)

        assertEquals(InstantiationError.UnsupportedThreadsModule, result.unwrapError())
        assertEquals(1, store.program.size)
        assertSame(existingInstruction, store.program.instructions[0])
        assertSame(existingTable, store.program.exceptionTable(0))
        assertEquals(0, existingFunction.callStrategy.entryIp)
        functions.forEach { assertEquals(-1, it.callStrategy.entryIp) }
    }
}
