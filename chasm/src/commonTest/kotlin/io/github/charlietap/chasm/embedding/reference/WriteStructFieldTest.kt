package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.transform.FieldValueEncoder
import io.github.charlietap.chasm.fixture.runtime.instance.structAddress
import io.github.charlietap.chasm.fixture.runtime.instance.structInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.structReferenceValue
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.runtime.ext.struct
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
        val instance = structInstance(
            structType = structType(listOf(fieldType)),
            fields = longArrayOf(117L),
        )
        val store = publicStore(store(structs = mutableListOf(instance)))
        val address = structAddress(0)
        val struct = structReferenceValue(address)
        val index = 0
        val value = FieldValue.Execution(i32(119))

        val fieldValueEncoder: FieldValueEncoder = { _value, _fieldType ->
            assertEquals(value, _value)
            assertEquals(fieldType, _fieldType)

            119L
        }

        val expected = Ok(Unit)

        val actual = internalWriteStructField(
            store = store,
            struct = struct,
            index = index,
            value = value,
            fieldValueEncoder = fieldValueEncoder,
        )

        assertEquals(expected, actual)
        assertEquals(119L, store.store.struct(address).fields[index])
    }
}
