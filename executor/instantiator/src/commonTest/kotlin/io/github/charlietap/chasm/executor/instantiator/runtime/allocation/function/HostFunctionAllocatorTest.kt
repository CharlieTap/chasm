package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.executor.runtime.store
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

        val expected = FunctionInstance.HostFunction(
            type = functionType.definedType(),
            functionType = functionType,
            function = hostFunction,
        )

        val address = HostFunctionAllocator(store, functionType, hostFunction)

        assertEquals(Address.Function(0), address)
        assertEquals(expected, functions[0])
    }
}
