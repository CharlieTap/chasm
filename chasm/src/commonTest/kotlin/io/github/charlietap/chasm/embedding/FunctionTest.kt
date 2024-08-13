package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicFunctionType
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FunctionTest {

    @Test
    fun `can allocate a function in the store and return an external value`() {

        val store = publicStore()
        val funcType = publicFunctionType()
        val hostFunction = object : HostFunction {
            override fun invoke(p1: List<Value>): List<Value> {
                return emptyList()
            }
        }

        val expectedType = functionType().definedType()
        val expected = publicFunction(functionExternalValue(functionAddress(0)))

        val actual = function(store, funcType, hostFunction)

        assertEquals(expected, actual)
        assertEquals(expectedType, store.store.functions[0].type)
        assertNotNull(store.store.functions[0])
    }
}
