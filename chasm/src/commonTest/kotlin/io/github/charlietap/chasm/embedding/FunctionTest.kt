package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.ir.type.functionType
import io.github.charlietap.chasm.type.ir.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FunctionTest {

    @Test
    fun `can allocate a function in the store and return an external value`() {

        val store = publicStore()
        val funcType = functionType()
        val hostFunction: HostFunction = {
            emptyList()
        }

        val expectedType = functionType().definedType()
        val expected = publicFunction(functionExternalValue(functionAddress(0)))

        val actual = function(store, funcType, hostFunction)

        assertEquals(expected, actual)
        assertEquals(expectedType, store.store.functions[0].type)
        assertNotNull(store.store.functions[0])
    }
}
