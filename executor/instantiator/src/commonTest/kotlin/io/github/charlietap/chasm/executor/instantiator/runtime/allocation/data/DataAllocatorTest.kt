package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.data

import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class DataAllocatorTest {

    @Test
    fun `can allocate a data instance`() {

        val data = mutableListOf<DataInstance>()
        val store = store(
            data = data,
        )

        val bytes = ubyteArrayOf()
        val expected = DataInstance(
            bytes = bytes,
        )

        val address = DataAllocator(store, bytes)

        assertEquals(Address.Data(0), address)
        assertEquals(expected, data[0])
    }
}
