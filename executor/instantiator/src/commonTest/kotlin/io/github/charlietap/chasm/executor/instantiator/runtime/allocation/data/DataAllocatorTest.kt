package io.github.charlietap.chasm.executor.instantiator.runtime.allocation.data

import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.runtime.address.Address
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
        val expected = dataInstance(
            bytes = bytes,
        )

        val address = DataAllocator(store, bytes)

        assertEquals(Address.Data(0), address)
        assertEquals(expected, data[0])
    }
}
