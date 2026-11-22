package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.runtime.ext.toLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReadTableTest {

    @Test
    fun `can read a reference value from a table`() {

        val functionAddress = functionAddress()
        val value = functionReferenceValue(functionAddress)
        val instance = tableInstance(elements = longArrayOf(functionReferenceValue(functionAddress).toLong()))
        val store = publicStore(store(tables = mutableListOf(instance)))
        val address = tableAddress(0)
        val table = publicTable(tableExternalValue(address), store.store)

        val actual = readTable(
            store = store,
            table = table,
            elementIndex = 0,
        )

        assertEquals(value, actual)
    }

    @Test
    fun `reading past the end of a table throws index out of bounds`() {

        val instance = tableInstance(elements = longArrayOf(functionReferenceValue(functionAddress()).toLong()))
        val store = publicStore(store(tables = mutableListOf(instance)))
        val table = publicTable(tableExternalValue(tableAddress(0)), store.store)

        assertFailsWith<IndexOutOfBoundsException> {
            readTable(store, table, 1)
        }
    }
}
