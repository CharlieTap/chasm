package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.transform.FieldValueEncoder
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.runtime.value.FieldValue
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
