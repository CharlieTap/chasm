package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.ir.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class TryTableExecutorTest {

    @Test
    fun `can thread handler payload destination slots into the exception handler`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(frame()),
        )
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        TryTableExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = ControlInstruction.TryTable(
                params = 0,
                results = 0,
                handlers = listOf(catchAllRefHandler(labelIndex(0))),
                instructions = emptyArray(),
                payloadDestinationSlots = listOf(listOf(5)),
            ),
        )

        assertEquals(listOf(listOf(5)), cstack.handlers().single().payloadDestinationSlots)
        assertEquals(1, cstack.labelsDepth())
        assertEquals(1, cstack.instructionsDepth())
    }
}
