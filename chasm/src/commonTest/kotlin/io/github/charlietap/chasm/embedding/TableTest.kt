package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.fixture.runtime.value.referenceValue
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TableTest {

    @Test
    fun `can allocate a table in the store and return an external value`() {

        val store = publicStore()
        val type = tableType()
        val initialValue = referenceValue()

        val expectedExternalType = ExternalValue.Table(Address.Table(0))
        val expected = publicTable(expectedExternalType)

        val actual = table(store, type, initialValue)

        assertEquals(expected, actual)
        assertEquals(tableType(), store.store.tables[0].type)
        assertEquals(
            0,
            store.store.tables[0]
                .elements.size,
        )
    }
}
