package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableTest {

    @Test
    fun `can allocate a table in the store and return an external value`() {

        val store = store()
        val type = tableType()
        val initialValue = ReferenceValue.Null(referenceType())

        val expected = ExternalValue.Table(Address.Table(0))

        val actual = table(store, type, initialValue)

        assertEquals(expected, actual)
        assertEquals(type, store.tables[0].type)
        assertEquals(0, store.tables[0].elements.size)
    }
}
