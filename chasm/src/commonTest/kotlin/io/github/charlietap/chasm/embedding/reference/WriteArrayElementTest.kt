package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.transform.FieldValueEncoder
import io.github.charlietap.chasm.fixture.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.bytePackedValue
import io.github.charlietap.chasm.fixture.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i8PackedType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.packedStorageType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.value.FieldValue
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteArrayElementTest {

    @Test
    fun `can write an element to an array`() {

        val fieldType = mutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
            mutability = varMutability(),
        )
        val instance = arrayInstance(
            arrayType = arrayType(fieldType),
            fields = longArrayOf(116L, 117L, 118L),
        )
        val store = publicStore(store(arrays = mutableListOf(instance)))
        val address = arrayAddress(0)
        val array = arrayReferenceValue(address)
        val value = executionFieldValue(i32(119))
        val index = 1

        val fieldValueEncoder: FieldValueEncoder = { _value, _fieldType ->
            assertEquals(value, _value)
            assertEquals(fieldType, _fieldType)

            119L
        }

        val expected = Ok(Unit)

        val actual = internalWriteArrayElement(
            store = store,
            array = array,
            index = index,
            value = value,
            fieldValueEncoder = fieldValueEncoder,
        )

        assertEquals(expected, actual)
        assertEquals(119L, store.store.array(address).fields[index])
    }

    @Test
    fun `can write a packed element to an array`() {

        val fieldType = mutableFieldType(
            storageType = packedStorageType(
                packedType = i8PackedType(),
            ),
            mutability = varMutability(),
        )
        val instance = arrayInstance(
            arrayType = arrayType(fieldType),
            fields = longArrayOf(0L),
        )
        val store = publicStore(store(arrays = mutableListOf(instance)))
        val address = arrayAddress(0)
        val array = arrayReferenceValue(address)
        val value = FieldValue.Packed(bytePackedValue(0x1FFL))

        val expected = ChasmResult.Success(Unit)

        val actual = writeArrayElement(
            store = store,
            array = array,
            index = 0,
            value = value,
        )

        assertEquals(expected, actual)
        assertEquals(0xFFL, store.store.array(address).fields[0])
    }
}
