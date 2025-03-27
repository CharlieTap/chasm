package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WasmFunctionAllocatorTest {

    @Test
    fun `can allocate a wasm function`() {

        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val functionType = functionType()
        val type = functionType.definedType()
        val rtt = rtt()
        val typeIndex = typeIndex(0)
        val wasmFunction = function(
            typeIndex = typeIndex,
        )
        val runtimeFunction = runtimeFunction(
            typeIndex = typeIndex,
        )

        val moduleInstance = moduleInstance(
            types = mutableListOf(type),
            runtimeTypes = listOf(rtt),
        )

        val functionInstruction = dispatchableInstruction()
        val callDispatcher: Dispatcher<ControlInstruction.WasmFunctionCall> = {
            functionInstruction
        }

        val expectedInstance = FunctionInstance.WasmFunction(
            type = type,
            rtt = rtt,
            functionType = functionType,
            function = runtimeFunction,
            module = moduleInstance,
        )

        val actual = WasmFunctionAllocator(
            moduleInstance = moduleInstance,
            function = wasmFunction,
            store = store,
            callDispatcher = callDispatcher,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(expectedInstance, functions[0])
        assertEquals(functionInstruction, store.instructions[0])
    }

    @Test
    fun `returns error if function type is not found`() {
        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val typeIndex = typeIndex(0)
        val wasmFunction = function(
            typeIndex = typeIndex,
        )

        val moduleInstance = moduleInstance()

        val expected = Err(
            InstantiationError.FailedToResolveFunctionType(
                typeIndex,
            ),
        )

        val actual = WasmFunctionAllocator(moduleInstance, wasmFunction, store)

        assertEquals(expected, actual)
        assertTrue(functions.isEmpty())
    }
}
