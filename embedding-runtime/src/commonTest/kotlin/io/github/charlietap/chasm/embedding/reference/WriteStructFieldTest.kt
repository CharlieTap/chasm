package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.transform.FieldValueEncoder
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.runtime.value.FieldValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WriteStructFieldTest {

    @Test
    fun `can write a field to a struct`() {

        val fieldType = mutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
            mutability = varMutability(),
        )
        val fixture = structFieldTestFixture(fieldType, 117L)
        val index = 0
        val value = FieldValue.Execution(i32(119))

        val fieldValueEncoder: FieldValueEncoder = { _value, _fieldType ->
            assertEquals(value, _value)
            assertEquals(fieldType, _fieldType)

            119L
        }

        val expected = Ok(Unit)

        val actual = internalWriteStructField(
            store = fixture.store,
            struct = fixture.reference,
            index = index,
            value = value,
            fieldValueEncoder = fieldValueEncoder,
        )

        assertEquals(expected, actual)
        assertEquals(119L, fixture.store.store.heap.getStructField(fixture.rawReference, index))
    }

    @Test
    fun `writing an immutable field throws illegal argument`() {

        val fieldType = immutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
            mutability = constMutability(),
        )
        val fixture = structFieldTestFixture(fieldType, 117L)

        assertFailsWith<IllegalArgumentException> {
            writeStructField(fixture.store, fixture.reference, 0, FieldValue.Execution(i32(119)))
        }
        assertEquals(117L, fixture.store.store.heap.getStructField(fixture.rawReference, 0))
    }

    @Test
    fun `writing a field past the end of a struct throws index out of bounds`() {

        val fieldType = mutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
            mutability = varMutability(),
        )
        val fixture = structFieldTestFixture(fieldType, 117L)

        assertFailsWith<IndexOutOfBoundsException> {
            writeStructField(fixture.store, fixture.reference, 1, FieldValue.Execution(i32(119)))
        }
    }
}
