package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryGrowerImplTest {

    @Test
    fun `can grow a memory instance`() {

        val additionalPages = LinearMemory.Pages(2)
        val limits = limits(1u)
        val type = memoryType(limits)

        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1))

        val instance = MemoryInstance(
            type = type,
            data = memory,
        )

        val expected = MemoryInstance(
            type = MemoryType(limits.copy(3u)),
            data = ByteArrayLinearMemory(LinearMemory.Pages(3)),
        )

        val actual = MemoryGrowerImpl(instance, additionalPages.amount)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `cannot grow a memory instance beyond max`() {

        val additionalPages = LinearMemory.Pages(2)
        val limits = limits(1u, 2u)
        val type = memoryType(limits)
        val error = InvocationError.MemoryGrowExceedsLimits(2, 2)

        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1), LinearMemory.Pages(2))

        val instance = MemoryInstance(
            type = type,
            data = memory,
        )

        val expected = Err(error)

        val actual = MemoryGrowerImpl(instance, additionalPages.amount)

        assertEquals(expected, actual)
    }
}
