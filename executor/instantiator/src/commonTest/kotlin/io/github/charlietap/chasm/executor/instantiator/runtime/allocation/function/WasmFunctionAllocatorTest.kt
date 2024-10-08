package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
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

        val type = functionType().definedType()
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

        val actual = WasmFunctionAllocator(store, moduleInstance, wasmFunction)

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

        val actual = WasmFunctionAllocator(store, moduleInstance, wasmFunction)

        assertEquals(expected, actual)
        assertTrue(functions.isEmpty())
    }
}
