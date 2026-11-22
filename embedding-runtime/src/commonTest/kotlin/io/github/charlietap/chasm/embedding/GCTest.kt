package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.fixture.registerArrayType
import io.github.charlietap.chasm.embedding.reference.allocateNumericArray
import io.github.charlietap.chasm.embedding.reference.arrayLength
import io.github.charlietap.chasm.embedding.reference.readArrayElements
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.runtime.type.RTT
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class GCTest {

    @Test
    fun `collect frees guest objects nothing references`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = registerArrayType(runtime, fieldType())
        val rawReference = runtime.heap.allocateArrayFromElements(RTT(type.id), LongArray(0), 0, 0)

        collect(store)

        assertEquals(-1, runtime.heap.arrayRuntimeTypeIdOrNegative(rawReference))
    }

    @Test
    fun `collect keeps the objects the host holds`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = registerArrayType(runtime, fieldType())
        val array = allocateNumericArray(store, type, longArrayOf(116L, 117L, 118L))

        collect(store)

        val elements = LongArray(3)
        readArrayElements(array, 0, elements)
        assertEquals(3, arrayLength(array))
        assertContentEquals(longArrayOf(116L, 117L, 118L), elements)
    }
}
