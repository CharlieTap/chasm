package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.fixture.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.bytePackedValue
import io.github.charlietap.chasm.fixture.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.packedFieldValue
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i8PackedType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.packedStorageType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.runtime.value.FieldValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadArrayElementTest {

    @Test
    fun `can read an element from an array`() {

        val fieldType = immutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
        )
        val instance = arrayInstance(
            arrayType = arrayType(fieldType),
            fields = longArrayOf(116L, 117L, 118L),
        )
        val store = publicStore(store(arrays = mutableListOf(instance)))
        val address = arrayAddress(0)
        val array = arrayReferenceValue(address)
        val index = 1
        val value = executionFieldValue(i32(117))

        val fieldValueDecoder: FieldValueDecoder = { _value, _fieldType ->
            assertEquals(117L, _value)
            assertEquals(fieldType, _fieldType)

            value
        }

        val expected = Ok(value)

        val actual = internalReadArrayElement(
            store = store,
            array = array,
            index = index,
            fieldValueDecoder = fieldValueDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can read a packed element from an array`() {

        val fieldType = immutableFieldType(
            storageType = packedStorageType(
                packedType = i8PackedType(),
            ),
        )
        val instance = arrayInstance(
            arrayType = arrayType(fieldType),
            fields = longArrayOf(117L),
        )
        val store = publicStore(store(arrays = mutableListOf(instance)))
        val address = arrayAddress(0)
        val array = arrayReferenceValue(address)

        val expected = ChasmResult.Success(
            packedFieldValue(bytePackedValue(117L)),
        )

        val actual = readArrayElement(
            store = store,
            array = array,
            index = 0,
        )

        assertEquals(expected, actual)
    }
}
