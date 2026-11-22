package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.fixture.type.arrayType
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadArrayLengthTest {

    @Test
    fun `can read the length of an array`() {

        val fixture = arrayFieldTestFixture(arrayType(), longArrayOf(116L, 117L, 118L))
        val store = fixture.store
        val array = fixture.reference

        val actual = readArrayLength(
            store = store,
            array = array,
        )

        assertEquals(3, actual)
    }
}
