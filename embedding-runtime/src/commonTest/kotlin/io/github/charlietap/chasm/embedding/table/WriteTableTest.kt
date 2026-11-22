package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WriteTableTest {

    @Test
    fun `can write a reference value to a table`() {

        val functionAddress = functionAddress()
        val value = functionReferenceValue(functionAddress)
        val instance = tableInstance(elements = longArrayOf(ReferenceValue.Null(AbstractHeapType.Func).toLongFromBoxed()))
        val store = publicStore(
            store(
                functions = mutableListOf(hostFunctionInstance()),
                tables = mutableListOf(instance),
            ),
        )
        val address = tableAddress(0)
        val table = publicTable(tableExternalValue(address), store.store)

        writeTable(
            store = store,
            table = table,
            elementIndex = 0,
            value = value,
        )

        assertEquals(functionReferenceValue(functionAddress).toLong(), store.store.tables[0].elements[0])
    }

    @Test
    fun `writing past the end of a table throws index out of bounds`() {

        val instance = tableInstance(elements = longArrayOf(ReferenceValue.Null(AbstractHeapType.Func).toLongFromBoxed()))
        val store = publicStore(
            store(
                functions = mutableListOf(hostFunctionInstance()),
                tables = mutableListOf(instance),
            ),
        )
        val table = publicTable(tableExternalValue(tableAddress(0)), store.store)

        assertFailsWith<IndexOutOfBoundsException> {
            writeTable(store, table, 1, functionReferenceValue(functionAddress()))
        }
    }

    @Test
    fun `writing null into a non nullable table throws illegal argument`() {

        val instance = tableInstance(
            type = tableType(referenceType = ReferenceType.Ref(AbstractHeapType.Func)),
            elements = longArrayOf(ReferenceValue.Null(AbstractHeapType.Func).toLongFromBoxed()),
        )
        val store = publicStore(store(tables = mutableListOf(instance)))
        val table = publicTable(tableExternalValue(tableAddress(0)), store.store)

        assertFailsWith<IllegalArgumentException> {
            writeTable(store, table, 0, ReferenceValue.Null(AbstractHeapType.Func))
        }
    }
}
