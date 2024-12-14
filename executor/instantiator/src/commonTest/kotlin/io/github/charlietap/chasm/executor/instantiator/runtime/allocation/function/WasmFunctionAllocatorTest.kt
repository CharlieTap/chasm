package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
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
        val context = instantiationContext(
            store = store,
        )

        val type = functionType().definedType()
        val typeIndex = typeIndex(0u)
        val wasmFunction = function(
            typeIndex = typeIndex,
        )
        val runtimeFunction = runtimeFunction(
            typeIndex = typeIndex,
        )

        val moduleInstance = moduleInstance(
            types = mutableListOf(type),
        )

        val expectedInstance = FunctionInstance.WasmFunction(
            type = type,
            function = runtimeFunction,
            module = moduleInstance,
        )

        val actual = WasmFunctionAllocator(
            context = context,
            moduleInstance = moduleInstance,
            function = wasmFunction,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(expectedInstance, functions[0])
    }

    @Test
    fun `returns error if function type is not found`() {
        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )
        val context = instantiationContext(
            store = store,
        )

        val typeIndex = typeIndex(0u)
        val wasmFunction = function(
            typeIndex = typeIndex,
        )

        val moduleInstance = moduleInstance()

        val expected = Err(
            InstantiationError.FailedToResolveFunctionType(
                typeIndex,
            ),
        )

        val actual = WasmFunctionAllocator(context, moduleInstance, wasmFunction)

        assertEquals(expected, actual)
        assertTrue(functions.isEmpty())
    }
}
