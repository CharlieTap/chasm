package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.fixture.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.runtime.value.FieldValue
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
