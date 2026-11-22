package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.fixture.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.runtime.value.FieldValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReadStructFieldTest {

    @Test
    fun `can read a field from a struct`() {

        val fieldType = immutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
        )
        val fixture = structFieldTestFixture(fieldType, 117L)
        val index = 0
        val value = executionFieldValue(i32(117))

        val fieldValueDecoder: FieldValueDecoder = { _value, _fieldType ->
            assertEquals(117L, _value)
            assertEquals(fieldType, _fieldType)

            value
        }

        val expected = Ok(value)

        val actual = internalReadStructField(
            store = fixture.store,
            struct = fixture.reference,
            index = index,
            fieldValueDecoder = fieldValueDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `reading a field past the end of a struct throws index out of bounds`() {

        val fieldType = immutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
        )
        val fixture = structFieldTestFixture(fieldType, 117L)

        assertFailsWith<IndexOutOfBoundsException> {
            readStructField(fixture.store, fixture.reference, 1)
        }
    }

    @Test
    fun `reading a struct of a dropped store throws illegal state`() {

        val fieldType = immutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
        )
        val fixture = structFieldTestFixture(fieldType, 117L)

        dropStore(fixture.store)

        assertFailsWith<IllegalStateException> {
            readStructField(fixture.store, fixture.reference, 0)
        }
    }
}
