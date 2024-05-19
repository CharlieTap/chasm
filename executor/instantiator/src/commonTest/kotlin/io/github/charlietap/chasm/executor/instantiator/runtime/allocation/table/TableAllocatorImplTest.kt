package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.table

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableAllocatorImplTest {

    @Test
    fun `can allocate a table instance`() {

        val tables = mutableListOf<TableInstance>()
        val store = store(
            tables = tables,
        )

        val min = 3
        val limits = limits(min.toUInt())
        val type = tableType(limits = limits)

        val refValue = ReferenceValue.Null(AbstractHeapType.Func)
        val elements = Array<ReferenceValue>(min) {
            refValue
        }

        val expected = TableInstance(
            type = type,
            elements = elements,
        )

        val address = TableAllocatorImpl(store, type, refValue)

        assertEquals(Address.Table(0), address)
        assertEquals(expected, tables[0])
    }
}
