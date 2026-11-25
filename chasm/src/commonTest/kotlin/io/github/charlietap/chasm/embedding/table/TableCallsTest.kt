package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.functionReference
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.refNonNullReferenceType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TableCallsTest {

    @Test
    fun `can read a reference value from a table`() {

        val store = interpreterStore()
        val reference = hostFunctionReference(store)
        val table = table(store, tableType(referenceType = refNullReferenceType(), limits = limits(min = 1u)), reference)

        val actual = readTable(table, 0)

        assertEquals(reference, actual)
    }

    @Test
    fun `reading past the end of a table throws index out of bounds`() {

        val table = nullableTable(interpreterStore(), 1u)

        assertFailsWith<IndexOutOfBoundsException> {
            readTable(table, 1)
        }
    }

    @Test
    fun `can write a reference value to a table`() {

        val store = interpreterStore()
        val reference = hostFunctionReference(store)
        val table = nullableTable(store, 1u)

        writeTable(table, 0, reference)

        assertEquals(reference, readTable(table, 0))
    }

    @Test
    fun `writing past the end of a table throws index out of bounds`() {

        val store = interpreterStore()
        val table = nullableTable(store, 1u)

        assertFailsWith<IndexOutOfBoundsException> {
            writeTable(table, 1, hostFunctionReference(store))
        }
    }

    @Test
    fun `writing null into a non nullable table throws illegal argument`() {

        val store = interpreterStore()
        val reference = hostFunctionReference(store)
        val table = table(store, tableType(referenceType = refNonNullReferenceType(AbstractHeapType.Func), limits = limits(min = 1u)), reference)

        assertFailsWith<IllegalArgumentException> {
            writeTable(table, 0, null)
        }
        assertEquals(reference, readTable(table, 0))
    }

    @Test
    fun `can size and grow a table`() {

        val store = interpreterStore()
        val reference = hostFunctionReference(store)
        val table = nullableTable(store, 1u)

        val previous = growTable(table, 2, reference)

        assertEquals(1, previous)
        assertEquals(3, sizeTable(table))
        assertNull(readTable(table, 0))
        assertEquals(reference, readTable(table, 2))
    }

    @Test
    fun `can read write fill and copy ranges of elements`() {

        val store = interpreterStore()
        val first = hostFunctionReference(store)
        val second = hostFunctionReference(store)
        val table = nullableTable(store, 6u)

        writeTableElements(table, 0, arrayOf(first, second))
        fillTable(table, 2, second, 2)
        copyTable(table, 0, table, 4, 2)

        val buffer = arrayOfNulls<Any>(7)
        readTableElements(table, buffer, 0, 6, 1)
        assertContentEquals(arrayOf(null, first, second, second, second, first, second), buffer)
    }

    private fun nullableTable(
        store: Store,
        size: ULong,
    ): Table {
        return table(store, tableType(referenceType = refNullReferenceType(AbstractHeapType.Func), limits = limits(min = size)), null)
    }

    private fun hostFunctionReference(store: Store): FunctionReference {
        return functionReference(function(store, functionType(), HostFunction { _, _ -> }))
    }
}
