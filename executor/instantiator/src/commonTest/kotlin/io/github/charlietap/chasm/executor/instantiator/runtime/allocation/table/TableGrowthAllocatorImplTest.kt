package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableGrowthAllocatorAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableGrowthAllocatorImplTest {

    @Test
    fun `can grow a table instance`() {

        val elementsToAdd = 2u
        val limits = limits(1u)
        val newMin = elementsToAdd + limits.min
        val refType = ReferenceType.RefNull(AbstractHeapType.Func)
        val refVal = ReferenceValue.Function(Address.Function(0))
        val type = tableType(refType, limits)

        val instance = TableInstance(
            type = type,
            elements = MutableList(limits.min.toInt()) { refVal },
        )

        val expected = TableInstance(
            type = type.copy(limits = limits.copy(newMin)),
            elements = MutableList(newMin.toInt()) { refVal },
        )

        val actual = TableGrowthAllocatorAllocatorImpl(instance, elementsToAdd, refVal)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot grow a table instance beyond max`() {

        val elementsToAdd = 2u
        val limits = limits(1u, 2u)
        val refType = ReferenceType.RefNull(AbstractHeapType.Func)
        val refVal = ReferenceValue.Function(Address.Function(0))
        val type = tableType(refType, limits)

        val instance = TableInstance(
            type = type,
            elements = MutableList(limits.min.toInt()) { refVal },
        )

        val expected = Err(InvocationError.TableGrowExceedsLimits(2u))

        val actual = TableGrowthAllocatorAllocatorImpl(instance, elementsToAdd, refVal)

        assertEquals(expected, actual)
    }
}
