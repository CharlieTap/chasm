package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.refNonNullReferenceType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.host.HostFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TableTest {

    @Test
    fun `can allocate a table in the store and return a handle`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = tableType(referenceType = refNullReferenceType())

        val actual = table(store, type, null)

        assertEquals(type, actual.type)
        assertEquals(type, runtime.tables[0].type)
        assertEquals(0, runtime.tables[0].elements.size)
    }

    @Test
    fun `every element of a new table holds the initial reference`() {

        val store = interpreterStore()
        val reference = functionReference(function(store, functionType(), HostFunction { _, _ -> }))
        val type = tableType(referenceType = refNonNullReferenceType(), limits = limits(min = 2u))

        val table = table(store, type, reference)

        assertEquals(reference, readTable(table, 0))
        assertEquals(reference, readTable(table, 1))
    }

    @Test
    fun `a null initial reference is rejected for a non nullable table`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = tableType(referenceType = refNonNullReferenceType(), limits = limits(min = 1u))

        assertFailsWith<IllegalArgumentException> {
            table(store, type, null)
        }
        assertEquals(0, runtime.tables.size)
    }
}
