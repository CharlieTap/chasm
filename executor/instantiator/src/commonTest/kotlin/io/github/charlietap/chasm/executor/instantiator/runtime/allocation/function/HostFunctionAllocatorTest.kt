package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunction
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionAllocatorTest {

    @Test
    fun `can allocate a host function`() {

        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val functionType = functionType()
        val hostFunction: HostFunction = { emptyList() }

        val rtt = store.heap.registerRuntimeType(functionType.definedType())

        val expectedInstance = FunctionInstance.HostFunction(
            rtt = rtt,
            functionType = functionType,
            function = hostFunction,
        )
        val expected = functionExternalValue(
            address = functionAddress(0),
        )

        val actual = HostFunctionAllocator(
            store = store,
            functionType = functionType,
            function = hostFunction,
        )

        assertEquals(expected, actual)
        assertEquals(expectedInstance, functions[0])
    }
}
