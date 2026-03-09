package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import kotlin.test.Test
import kotlin.test.assertEquals

class BreakExecutorTest {

    @Test
    fun `does not shrink the value stack for frame slot branches`() {
        val continuation = dispatchableInstruction()
        val cstack = cstack(
            frames = listOf(
                frame(
                    frameSlotMode = true,
                ),
            ),
            labels = listOf(
                label(
                    arity = 1,
                    depths = stackDepths(values = 1),
                    continuation = continuation,
                ),
            ),
        )
        val vstack = vstack()

        vstack.push(99L)
        vstack.framePointer = 1
        vstack.reserveFrame(4)

        val depthBefore = vstack.depth()

        BreakExecutor(
            controlStack = cstack,
            valueStack = vstack,
            labelIndex = labelIndex(0),
        )

        assertEquals(depthBefore, vstack.depth())
        assertEquals(1, cstack.instructionsDepth())
        assertEquals(0, cstack.labelsDepth())
    }
}
