package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.table

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ast.type.limits
import io.github.charlietap.chasm.fixture.ast.type.tableType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TableAllocatorTest {

    @Test
    fun `can allocate a table instance`() {

        val tables = mutableListOf<TableInstance>()
        val store = store(
            tables = tables,
        )

        val min = 3
        val limits = limits(min.toUInt())
        val type = tableType(limits = limits)

        val refValue = nullReferenceValue(AbstractHeapType.Func).toLong()
        val elements = LongArray(min) {
            refValue
        }

        val expected = tableInstance(
            type = type,
            elements = elements,
        )

        val address = TableAllocator(store, type, refValue)

        assertEquals(Address.Table(0), address)
        assertEquals(expected, tables[0])
    }
}
