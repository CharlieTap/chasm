package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WasmFunctionAllocatorImplTest {

    @Test
    fun `can allocate a wasm function`() {

        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val type = functionType()
        val wasmFunction = function(
            typeIndex = Index.TypeIndex(0u),
        )

        val moduleInstance = moduleInstance(
            types = mutableListOf(type),
        )

        val expectedInstance = FunctionInstance.WasmFunction(
            type = type,
            function = wasmFunction,
            module = moduleInstance,
        )

        val actual = WasmFunctionAllocatorImpl(store, moduleInstance, wasmFunction)

        assertEquals(Ok(Address.Function(0)), actual)
        assertEquals(expectedInstance, functions[0])
    }

    @Test
    fun `returns error if function type is not found`() {
        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val typeIndex = Index.TypeIndex(0u)
        val wasmFunction = function(
            typeIndex = typeIndex,
        )

        val moduleInstance = moduleInstance()

        val expected = Err(
            InstantiationError.FailedToResolveFunctionType(
                typeIndex,
            ),
        )

        val actual = WasmFunctionAllocatorImpl(store, moduleInstance, wasmFunction)

        assertEquals(expected, actual)
        assertTrue(functions.isEmpty())
    }
}
