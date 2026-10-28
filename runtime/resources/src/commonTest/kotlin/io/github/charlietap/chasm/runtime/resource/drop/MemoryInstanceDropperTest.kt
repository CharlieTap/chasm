package io.github.charlietap.chasm.runtime.resource.drop

import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.memory.NoOpLinearMemory
import io.github.charlietap.chasm.fixture.runtime.memory.linearMemory
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import io.github.charlietap.chasm.host.ByteArrayHostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.type.Limits
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemoryInstanceDropperTest {

    @Test
    fun `calling MemoryInstanceDropper sets bounds to zero and invokes linear memory destructor`() {
        val linearMemory = linearMemory()
        val memoryType = memoryType(
            limits = limits(4u, 8u),
            shared = unsharedStatus(),
        )
        val memoryInstance = memoryInstance(
            type = memoryType,
            data = linearMemory,
        )
        assertEquals(PAGE_SIZE * 4, memoryInstance.size) // set size cache

        var destructorCalled = false
        val destructor: LinearMemoryDestructor = { _data ->
            assertEquals(linearMemory, _data)
            destructorCalled = true
        }

        MemoryInstanceDropper(memoryInstance, destructor)

        assertEquals(Limits(0u, 0u), memoryInstance.type.limits)
        assertEquals(0, memoryInstance.size) // test cache refresh
        assertTrue(destructorCalled)
    }

    @Test
    fun `a dropped memory's byte array is no longer held`() {
        val linearMemory = ArrayMemory(ByteArray(PAGE_SIZE + MemoryInstance.REQUIRED_OVERREAD_BYTES))
        val memoryInstance = memoryInstance(
            type = memoryType(
                limits = limits(1u, 1u),
                shared = unsharedStatus(),
            ),
            data = linearMemory,
        )
        assertNotNull(memoryInstance.byteArray)

        MemoryInstanceDropper(memoryInstance) { linearMemory.bytes = ByteArray(0) }

        assertNull(memoryInstance.byteArray)
    }

    @OptIn(UnsafeHostApi::class)
    private class ArrayMemory(
        var bytes: ByteArray,
    ) : LinearMemory by NoOpLinearMemory,
        ByteArrayHostMemory {
        override val byteSize: Int get() = bytes.size

        override fun unsafeBorrowByteArray(): ByteArray = bytes
    }
}
