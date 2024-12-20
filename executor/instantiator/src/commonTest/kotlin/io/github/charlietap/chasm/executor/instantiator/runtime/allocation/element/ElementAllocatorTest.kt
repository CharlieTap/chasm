package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.element

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementAllocatorTest {

    @Test
    fun `can allocate a element instance`() {

        val elements = mutableListOf<ElementInstance>()
        val store = store(
            elements = elements,
        )

        val refType = refNullReferenceType(AbstractHeapType.Func)

        val expected = elementInstance(
            type = refType,
            elements = arrayOf(),
        )

        val address = ElementAllocator(store, refType, emptyList())

        assertEquals(Address.Element(0), address)
        assertEquals(expected, elements[0])
    }
}
