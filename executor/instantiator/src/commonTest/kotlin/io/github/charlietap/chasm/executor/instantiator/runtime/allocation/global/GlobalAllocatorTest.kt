package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.global

import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ast.type.globalType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
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
        val value = executionValue().toLong()

        val expected = globalInstance(
            type = type,
            value = value,
        )

        val address = GlobalAllocator(store, type, value)

        assertEquals(Address.Global(0), address)
        assertEquals(expected, globals[0])
    }
}
