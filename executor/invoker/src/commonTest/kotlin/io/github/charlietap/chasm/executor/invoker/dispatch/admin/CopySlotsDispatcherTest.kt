package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class CopySlotsDispatcherTest {

    @Test
    fun `copies overlapping slots in order`() {
        val vstack = vstack().apply {
            reserveFrame(3)
            setFrameSlot(0, 1)
            setFrameSlot(1, 2)
            setFrameSlot(2, 3)
        }
        val cstack = cstack()
        val store = store()

        val nextIp = CopySlotSequenceDispatcher(
            sourceSlots = intArrayOf(0, 1),
            destinationSlots = intArrayOf(1, 2),
        )(
            vstack,
            cstack,
            store,
            executionContext(cstack, vstack, store),
            17,
        )

        assertEquals(17, nextIp)
        assertEquals(1L, vstack.getFrameSlot(0))
        assertEquals(1L, vstack.getFrameSlot(1))
        assertEquals(1L, vstack.getFrameSlot(2))
    }
}
