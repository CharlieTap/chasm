package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemArg
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MemoryStoreExecutorTest {

    @Test
    fun `stores at an unsigned address above the signed range trap instead of wrapping`() {
        // 0xFFFFFFFC + 8 wraps to 4 in 32-bit arithmetic, which is in bounds.
        assertStoresTrap(address = -4, offset = 8)
    }

    @Test
    fun `stores with an unsigned offset above the signed range trap instead of wrapping`() {
        // 8 + 0xFFFFFFFC wraps to 4 in 32-bit arithmetic, which is in bounds.
        assertStoresTrap(address = 8, offset = -4)
    }

    @Test
    fun `stores whose address and offset overflow a signed int trap`() {
        assertStoresTrap(address = Int.MAX_VALUE - 4, offset = 8)
    }

    @Test
    fun `stores crossing the end of memory trap`() {
        // The last byte is in bounds, so a byte store here would succeed.
        assertStoresTrap(address = PAGE_SIZE - 9, offset = 8, includeByteStores = false)
    }

    private fun assertStoresTrap(address: Int, offset: Int, includeByteStores: Boolean = true) {
        val memArg = MemArg(offset)
        val byteStore: (ValueStack, MemoryInstance) -> Unit = { vstack, memory ->
            I32Store8Executor(vstack, executionContext(vstack = vstack), MemoryInstruction.I32Store8Ss(1, 0, memory, memArg))
        }
        val stores: List<(ValueStack, MemoryInstance) -> Unit> = listOfNotNull(
            { vstack, memory ->
                I32StoreExecutor(vstack, executionContext(vstack = vstack), MemoryInstruction.I32StoreIi(-1, address, memory, memArg))
            },
            { vstack, memory ->
                I32StoreExecutor(vstack, executionContext(vstack = vstack), MemoryInstruction.I32StoreSs(1, 0, memory, memArg))
            },
            byteStore.takeIf { includeByteStores },
            { vstack, memory ->
                I64Store16Executor(vstack, executionContext(vstack = vstack), MemoryInstruction.I64Store16Ii(-1L, address, memory, memArg))
            },
        )

        stores.forEach { store ->
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
                    store(vstack, memory)
                }

                assertEquals(InvocationError.MemoryOperationOutOfBounds, failure.error)
                assertMemoryUnchanged(backing)
            } finally {
                LinearMemoryDestructor(backing)
            }
        }
    }

    private fun assertMemoryUnchanged(memory: LinearMemory) {
        for (address in 0 until PAGE_SIZE step Long.SIZE_BYTES) {
            assertEquals(0L, I64Reader(memory, address), "memory changed at $address")
        }
    }
}
