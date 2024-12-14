package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicReferenceValue
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.fixture.publicTableType
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ast.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableTest {

    @Test
    fun `can allocate a table in the store and return an external value`() {

        val store = publicStore()
        val type = publicTableType()
        val initialValue = publicReferenceValue()

        val expectedExternalType = ExternalValue.Table(Address.Table(0))
        val expected = publicTable(expectedExternalType)

        val actual = table(store, type, initialValue)

        assertEquals(expected, actual)
        assertEquals(tableType(), store.store.tables[0].type)
        assertEquals(0, store.store.tables[0].elements.size)
    }
}
