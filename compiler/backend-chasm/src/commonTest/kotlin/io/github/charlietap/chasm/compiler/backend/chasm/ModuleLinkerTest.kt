package io.github.charlietap.chasm.compiler.backend.chasm

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ModuleLinkerTest {

    @Test
    fun `failed linking removes new code and entries while preserving installed functions`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(
                function(body = Expression(ControlInstruction.Nop)),
                function(idx = Index.FunctionIndex(1u), body = Expression(ControlInstruction.Call(Index.FunctionIndex(2u)))),
                function(idx = Index.FunctionIndex(2u), body = Expression(ControlInstruction.Nop)),
            ),
        )
        val compiled = ModuleCompiler(module).unwrap()
        val linked = CirModule(functions = compiled.functions.take(2))
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

        assertFailsWith<IndexOutOfBoundsException> { ModuleLinker(store, instance, module, linked) }

        assertEquals(1, store.program.size)
        assertSame(existingInstruction, store.program.instructions[0])
        assertSame(existingTable, store.program.exceptionTable(0))
        assertEquals(0, existingFunction.callStrategy.entryIp)
        functions.forEach { assertEquals(-1, it.callStrategy.entryIp) }
    }

    @Test
    fun `a function that fails to link leaves the program as it was`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(
                function(body = Expression(ControlInstruction.Nop)),
                function(idx = Index.FunctionIndex(1u), body = Expression(ControlInstruction.Nop)),
            ),
        )
        val code = ModuleCompiler(module).unwrap()
        val store = store()
        val existingInstruction = dispatchableInstruction()
        store.program.append(existingInstruction)
        val instance = moduleInstance(
            runtimeTypes = store.heap.registerRuntimeTypes(module.definedTypes),
            functionAddresses = mutableListOf(functionAddress(0), functionAddress(1)),
        )
        val functions = List(2) {
            wasmFunctionInstance(module = instance).apply { callStrategy.entryIp = -1 }
        }
        store.functions.addAll(functions)
        val failure = IllegalStateException("cannot link")
        val linked = ArrayList<Int>()

        val thrown = assertFailsWith<IllegalStateException> {
            ModuleLinker(
                store = store,
                instance = instance,
                module = module,
                code = code,
                layoutBuilder = ::ModuleLayoutBuilder,
                functionLinker = { context, function, entryIp, output, baseIp ->
                    linked += function.index.index
                    if (function.index.index == 1) throw failure
                    FunctionLinker(context, function, entryIp, output, baseIp)
                },
                programInstaller = ::ModuleProgramInstaller,
            )
        }

        assertSame(failure, thrown)
        assertEquals(listOf(0, 1), linked)
        assertEquals(1, store.program.size)
        assertSame(existingInstruction, store.program.instructions[0])
        functions.forEach { assertEquals(-1, it.callStrategy.entryIp) }
    }
}
