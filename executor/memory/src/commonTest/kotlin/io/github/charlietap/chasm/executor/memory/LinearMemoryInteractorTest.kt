package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.fixture.memory.linearMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearMemoryInteractorTest {

    @Test
    fun `can run an operation on linear memory if the operation is in bounds `() {

        val memory = linearMemory(LinearMemory.Pages(1))
        val actual = LinearMemoryInteractorImpl(memory, 0, 4) {}

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `return error is operation is out of bounds `() {

        val memory = linearMemory(LinearMemory.Pages(1))
        val actual = LinearMemoryInteractorImpl(memory, LinearMemory.PAGE_SIZE, 1) {}

        assertEquals(Err(InvocationError.MemoryOperationOutOfBounds), actual)
    }
}
