package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.instance.memoryInstance
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.sharedStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryGrowerTest {

    @Test
    fun `can grow a memory instance`() {

        val additionalPages = LinearMemory.Pages(2)
        val limits = limits(1u)
        val status = sharedStatus()
        val type = memoryType(limits, status)

        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1))

        val instance = memoryInstance(
            type = type,
            data = memory,
        )

        val expected = memoryInstance(
            type = memoryType(limits.copy(3u), status),
            data = ByteArrayLinearMemory(LinearMemory.Pages(3)),
        )

        val actual = MemoryInstanceGrower(instance, additionalPages.amount)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot grow a memory instance beyond max`() {

        val additionalPages = LinearMemory.Pages(2)
        val limits = limits(1u, 2u)
        val type = memoryType(limits)
        val error = InvocationError.MemoryGrowExceedsLimits(2, 2)

        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1))

        val instance = memoryInstance(
            type = type,
            data = memory,
        )

        val expected = Err(error)

        val actual = MemoryInstanceGrower(instance, additionalPages.amount)

        assertEquals(expected, actual)
    }
}
