package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.fixture.registerStructType
import io.github.charlietap.chasm.embedding.internal.HeldStruct
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FieldType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class StructCallsTest {

    @Test
    fun `can read a field from a struct`() {

        val fixture = StructFixture(listOf(i32Field()), longArrayOf(117L))

        val actual = readStructField(fixture.struct, 0)

        assertEquals(117L, actual)
    }

    @Test
    fun `reading a field past the end of a struct throws index out of bounds`() {

        val fixture = StructFixture(listOf(i32Field()), longArrayOf(117L))

        assertFailsWith<IndexOutOfBoundsException> {
            readStructField(fixture.struct, 1)
        }
    }

    @Test
    fun `reading a struct of a dropped store throws illegal state`() {

        val fixture = StructFixture(listOf(i32Field()), longArrayOf(117L))

        dropStore(fixture.store)

        assertFailsWith<IllegalStateException> {
            readStructField(fixture.struct, 0)
        }
    }

    @Test
    fun `can write a field to a struct`() {

        val fixture = StructFixture(listOf(i32Field()), longArrayOf(117L))

        writeStructField(fixture.struct, 0, 119L)

        assertEquals(119L, fixture.heapField(0))
    }

    @Test
    fun `writing an immutable field throws illegal argument`() {

        val field = immutableFieldType(storageType = valueStorageType(i32ValueType()), mutability = constMutability())
        val fixture = StructFixture(listOf(field), longArrayOf(117L))

        assertFailsWith<IllegalArgumentException> {
            writeStructField(fixture.struct, 0, 119L)
        }
        assertEquals(117L, fixture.heapField(0))
    }

    @Test
    fun `writing a field past the end of a struct throws index out of bounds`() {

        val fixture = StructFixture(listOf(i32Field()), longArrayOf(117L))

        assertFailsWith<IndexOutOfBoundsException> {
            writeStructField(fixture.struct, 1, 119L)
        }
    }

    @Test
    fun `numeric and reference fields refuse the other lane`() {

        val fixture = StructFixture(listOf(i32Field(), externField()), longArrayOf(117L, 0L))

        assertFailsWith<IllegalArgumentException> { readStructReference(fixture.struct, 0) }
        assertFailsWith<IllegalArgumentException> { writeStructReference(fixture.struct, 0, null) }
        assertFailsWith<IllegalArgumentException> { readStructField(fixture.struct, 1) }
        assertFailsWith<IllegalArgumentException> { writeStructField(fixture.struct, 1, 1L) }
    }

    @Test
    fun `can read and write a host object in a reference field`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val host = Any()
        val type = registerStructType(runtime, listOf(i32Field(), externField()))
        val struct = allocateStruct(store, type, longArrayOf(117L, 0L), arrayOf(null, host))

        assertEquals(117L, readStructField(struct, 0))
        assertSame(host, readStructReference(struct, 1))

        writeStructReference(struct, 1, null)

        assertNull(readStructReference(struct, 1))
    }

    @Test
    fun `the struct type names the type it was allocated with`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = registerStructType(runtime, listOf(i32Field(), externField()))

        val struct = allocateStruct(store, type, LongArray(2), arrayOfNulls(2))

        assertEquals(type, structType(struct))
        assertEquals(2, structFieldCount(store, type))
    }

    private fun i32Field(): FieldType {
        return mutableFieldType(storageType = valueStorageType(i32ValueType()), mutability = varMutability())
    }

    private fun externField(): FieldType {
        val externref = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
        return mutableFieldType(storageType = valueStorageType(externref), mutability = varMutability())
    }

    private class StructFixture(
        fields: List<FieldType>,
        values: LongArray,
    ) {

        private val runtime: RuntimeStore = store()
        val store = interpreterStore(runtime)
        val struct: StructReference = allocateStruct(store, registerStructType(runtime, fields), values, arrayOfNulls(fields.size))

        fun heapField(index: Int): Long {
            return runtime.heap.getStructField((struct as HeldStruct).raw, index)
        }
    }
}
