package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.fixture.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadArrayLengthTest {

    @Test
    fun `can read the length of an array`() {

        val instance = arrayInstance(fields = longArrayOf(116L, 117L, 118L))
        val store = publicStore(store(arrays = mutableListOf(instance)))
        val address = arrayAddress(0)
        val array = arrayReferenceValue(address)

        val expected = ChasmResult.Success(3)

        val actual = readArrayLength(
            store = store,
            array = array,
        )

        assertEquals(expected, actual)
    }
}
