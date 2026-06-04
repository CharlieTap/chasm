package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.fixture.runtime.instance.structAddress
import io.github.charlietap.chasm.fixture.runtime.instance.structInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.structReferenceValue
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.structType
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
        val instance = structInstance(
            structType = structType(listOf(fieldType)),
            fields = longArrayOf(117L),
        )
        val store = publicStore(store(structs = mutableListOf(instance)))
        val address = structAddress(0)
        val struct = structReferenceValue(address)
        val index = 0
        val value = executionFieldValue(i32(117))

        val fieldValueDecoder: FieldValueDecoder = { _value, _fieldType ->
            assertEquals(117L, _value)
            assertEquals(fieldType, _fieldType)

            value
        }

        val expected = Ok(value)

        val actual = internalReadStructField(
            store = store,
            struct = struct,
            index = index,
            fieldValueDecoder = fieldValueDecoder,
        )

        assertEquals(expected, actual)
    }
}
