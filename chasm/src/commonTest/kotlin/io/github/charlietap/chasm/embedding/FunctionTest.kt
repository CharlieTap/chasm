package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.type.ext.definedType
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTest {

    @Test
    fun `can allocate a function in the store and return an external value`() {

        val store = store()
        val funcType = functionType()
        val hostFunction: HostFunction = { emptyList() }

        val expectedType = funcType.definedType()
        val expected = ExternalValue.Function(Address.Function(0))

        val actual = function(store, funcType, hostFunction)

        assertEquals(expected, actual)
        assertEquals(expectedType, store.functions[0].type)
        assertEquals(hostFunction, (store.functions[0] as? FunctionInstance.HostFunction)?.function)
    }
}
