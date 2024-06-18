package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.function

import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionAllocatorImplTest {

    @Test
    fun `can allocate a host function`() {

        val functions = mutableListOf<FunctionInstance>()
        val store = store(
            functions = functions,
        )

        val type = functionType()
        val hostFunction: HostFunction = { emptyList() }

        val expected = FunctionInstance.HostFunction(
            type = type.definedType(),
            function = hostFunction,
        )

        val address = HostFunctionAllocatorImpl(store, type, hostFunction)

        assertEquals(Address.Function(0), address)
        assertEquals(expected, functions[0])
    }
}
