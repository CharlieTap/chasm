package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.element

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementAllocatorTest {

    @Test
    fun `can allocate a element instance`() {

        val elements = mutableListOf<ElementInstance>()
        val store = store(
            elements = elements,
        )

        val refType = ReferenceType.RefNull(AbstractHeapType.Func)

        val expected = ElementInstance(
            type = refType,
            elements = arrayOf(),
        )

        val address = ElementAllocator(store, refType, emptyList())

        assertEquals(Address.Element(0), address)
        assertEquals(expected, elements[0])
    }
}
