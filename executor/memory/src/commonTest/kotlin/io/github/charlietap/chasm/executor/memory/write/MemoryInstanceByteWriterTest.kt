package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryInstanceByteWriterTest {

    @Test
    fun `can write a byte to linear memory `() {

        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1))
        val instance = memoryInstance(data = memory, type = memoryType(limits(1u)))

        val actual = MemoryInstanceByteWriter(instance, 117, 117, ::PessimisticBoundsChecker)

        assertEquals(Ok(Unit), actual)
        assertEquals(117, memory.memory[117])
    }

    @Test
    fun `return error is operation is out of bounds `() {

        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1))
        val instance = memoryInstance(data = memory)

        val actual = MemoryInstanceByteWriter(instance, LinearMemory.PAGE_SIZE, 117, ::PessimisticBoundsChecker)

        assertEquals(Err(InvocationError.MemoryOperationOutOfBounds), actual)
    }
}
