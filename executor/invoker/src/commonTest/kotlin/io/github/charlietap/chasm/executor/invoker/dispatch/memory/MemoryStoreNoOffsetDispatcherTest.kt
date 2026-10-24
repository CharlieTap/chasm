package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemArg
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MemoryStoreNoOffsetDispatcherTest {

    @Test
    fun `stores at an unsigned address above the signed range trap without an offset`() {
        val memArg = MemArg(0)
        val dispatchers: List<(MemoryInstance) -> DispatchableInstruction> = listOf(
            { memory -> I32StoreDispatcher(MemoryInstruction.I32StoreSs(1, 0, memory, memArg)) },
            { memory -> I32Store8Dispatcher(MemoryInstruction.I32Store8Is(-1, 0, memory, memArg)) },
        )

        // 0xFFFFFFFC and 0xFFFFFFFF are both far beyond a one page memory.
        listOf(-4, -1).forEach { address ->
            dispatchers.forEach { dispatcher ->
                val backing = LinearMemoryFactory(LinearMemory.Pages(1u))
                val memory = memoryInstance(
                    type = memoryType(limits = limits(min = 1u)),
                    data = backing,
                )
                val vstack = vstack().apply {
                    reserveDepth(2)
                    setFrameSlot(0, address.toLong())
                    setFrameSlot(1, -1L)
                }

                try {
                    val failure = assertFailsWith<InvocationException> {
                        dispatcher(memory)(vstack, executionContext(vstack, store()), 0)
                    }

                    assertEquals(InvocationError.MemoryOperationOutOfBounds, failure.error)
                    assertMemoryUnchanged(backing)
                } finally {
                    LinearMemoryDestructor(backing)
                }
            }
        }
    }

    private fun assertMemoryUnchanged(memory: LinearMemory) {
        for (address in 0 until PAGE_SIZE step Long.SIZE_BYTES) {
            assertEquals(0L, I64Reader(memory, address), "memory changed at $address")
        }
    }
}
