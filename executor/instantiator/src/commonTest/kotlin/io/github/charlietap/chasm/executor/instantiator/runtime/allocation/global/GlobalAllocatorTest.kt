package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.global

import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalAllocatorTest {

    @Test
    fun `can allocate a global instance`() {

        val globals = mutableListOf<GlobalInstance>()
        val store = store(
            globals = globals,
        )

        val type = globalType()
        val value = executionValue().toLongFromBoxed()

        val expected = globalInstance(
            type = type,
            value = value,
        )

        val address = GlobalAllocator(store, type, value)

        assertEquals(Address.Global(0), address)
        assertEquals(expected, globals[0])
    }
}
