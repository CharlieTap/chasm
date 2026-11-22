package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.fixture.registerArrayType
import io.github.charlietap.chasm.embedding.internal.HeldArray
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i8PackedType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.packedStorageType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FieldType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class ArrayCallsTest {

    @Test
    fun `can read an element from an array`() {

        val fixture = ArrayFixture(i32Field(), longArrayOf(116L, 117L, 118L))

        val actual = readArrayElement(fixture.array, 1)

        assertEquals(117L, actual)
    }

    @Test
    fun `can read a packed element from an array`() {

        val fixture = ArrayFixture(i8Field(), longArrayOf(117L))

        val actual = readArrayElement(fixture.array, 0)

        assertEquals(117L, actual)
    }

    @Test
    fun `reading past the end of an array throws index out of bounds`() {

        val fixture = ArrayFixture(i32Field(), longArrayOf(116L, 117L, 118L))

        assertFailsWith<IndexOutOfBoundsException> {
            readArrayElement(fixture.array, 3)
        }
    }

    @Test
    fun `can read the length of an array`() {

        val fixture = ArrayFixture(i32Field(), longArrayOf(116L, 117L, 118L))

        val actual = arrayLength(fixture.array)

        assertEquals(3, actual)
    }

    @Test
    fun `can write an element to an array`() {

        val fixture = ArrayFixture(i32Field(), longArrayOf(116L, 117L, 118L))

        writeArrayElement(fixture.array, 1, 119L)

        assertEquals(119L, fixture.heapElement(1))
    }

    @Test
    fun `a packed element reads back at its field width`() {

        val fixture = ArrayFixture(i8Field(), longArrayOf(0L))

        writeArrayElement(fixture.array, 0, 0x1FFL)

        assertEquals(0xFFL, readArrayElement(fixture.array, 0))
    }

    @Test
    fun `packed elements read in bulk come back at their field width`() {

        val fixture = ArrayFixture(i8Field(), longArrayOf(0x1FFL, 0x180L))
        val destination = LongArray(2)

        readArrayElements(fixture.array, 0, destination, 0, 2)

        assertContentEquals(longArrayOf(0xFFL, 0x80L), destination)
    }

    @Test
    fun `writing an immutable array throws illegal argument`() {

        val field = immutableFieldType(storageType = valueStorageType(i32ValueType()), mutability = constMutability())
        val fixture = ArrayFixture(field, longArrayOf(116L))

        assertFailsWith<IllegalArgumentException> {
            writeArrayElement(fixture.array, 0, 119L)
        }
        assertEquals(116L, fixture.heapElement(0))
    }

    @Test
    fun `writing past the end of an array throws index out of bounds`() {

        val fixture = ArrayFixture(i32Field(), longArrayOf(116L))

        assertFailsWith<IndexOutOfBoundsException> {
            writeArrayElement(fixture.array, 1, 119L)
        }
    }

    @Test
    fun `numeric and reference arrays refuse the other lane`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val numbers = allocateNumericArray(store, registerArrayType(runtime, i32Field()), 1, 0L)
        val references = allocateReferenceArray(store, registerArrayType(runtime, externField()), 1, null)

        assertFailsWith<IllegalArgumentException> { readArrayReference(numbers, 0) }
        assertFailsWith<IllegalArgumentException> { writeArrayReference(numbers, 0, null) }
        assertFailsWith<IllegalArgumentException> { readArrayElement(references, 0) }
        assertFailsWith<IllegalArgumentException> { writeArrayElement(references, 0, 1L) }
        assertFailsWith<IllegalArgumentException> { allocateNumericArray(store, registerArrayType(runtime, externField()), 1, 0L) }
        assertFailsWith<IllegalArgumentException> { allocateReferenceArray(store, registerArrayType(runtime, i32Field()), 1, null) }
    }

    @Test
    fun `can read and write host objects in a reference array`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val host = Any()
        val array = allocateReferenceArray(store, registerArrayType(runtime, externField()), arrayOf(null, host))

        assertNull(readArrayReference(array, 0))
        assertSame(host, readArrayReference(array, 1))

        writeArrayReference(array, 0, host)

        assertSame(host, readArrayReference(array, 0))
    }

    @Test
    fun `can read write fill and copy ranges of elements`() {

        val fixture = ArrayFixture(i32Field(), LongArray(6))

        writeArrayElements(fixture.array, 0, longArrayOf(1L, 2L))
        fillArray(fixture.array, 2, 2, 3L)
        copyArray(fixture.array, 0, fixture.array, 4, 2)

        val elements = LongArray(7)
        readArrayElements(fixture.array, 0, elements, 1, 6)
        assertContentEquals(longArrayOf(0L, 1L, 2L, 3L, 3L, 1L, 2L), elements)
    }

    @Test
    fun `writing an array of a dropped store throws illegal state`() {

        val fixture = ArrayFixture(i32Field(), longArrayOf(116L))

        dropStore(fixture.store)

        assertFailsWith<IllegalStateException> {
            writeArrayElement(fixture.array, 0, 119L)
        }
    }

    private fun i32Field(): FieldType {
        return mutableFieldType(storageType = valueStorageType(i32ValueType()), mutability = varMutability())
    }

    private fun i8Field(): FieldType {
        return mutableFieldType(storageType = packedStorageType(i8PackedType()), mutability = varMutability())
    }

    private fun externField(): FieldType {
        val externref = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
        return mutableFieldType(storageType = valueStorageType(externref), mutability = varMutability())
    }

    private class ArrayFixture(
        field: FieldType,
        elements: LongArray,
    ) {

        private val runtime: RuntimeStore = store()
        val store = interpreterStore(runtime)
        val array: ArrayReference = allocateNumericArray(store, registerArrayType(runtime, field), elements)

        fun heapElement(index: Int): Long {
            return runtime.heap.getArrayElement((array as HeldArray).raw, index)
        }
    }
}
