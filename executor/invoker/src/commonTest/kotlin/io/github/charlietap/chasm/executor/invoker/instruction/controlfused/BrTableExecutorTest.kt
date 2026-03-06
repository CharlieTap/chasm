package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class BrTableExecutorTest {

    @Test
    fun `can move selected branch values into target frame slots`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(
                frame(
                    frameSlotMode = true,
                ),
            ),
            labels = listOf(
                label(
                    arity = 1,
                    depths = stackDepths(values = 0),
                ),
            ),
        )
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        vstack.reserveFrame(5)
        vstack.setFrameSlot(3, 41L)

        BrTableExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = FusedControlInstruction.BrTableI(
                operand = 0,
                labelIndices = listOf(labelIndex(0)),
                defaultLabelIndex = labelIndex(0),
                takenInstructions = listOf(
                    listOf(
                        { valueStack, _, _, _ ->
                            valueStack.setFrameSlot(1, valueStack.getFrameSlot(3))
                        },
                    ),
                ),
                defaultTakenInstructions = listOf(
                    { valueStack, _, _, _ ->
                        valueStack.setFrameSlot(2, valueStack.getFrameSlot(3))
                    },
                ),
            ),
        )

        assertEquals(41L, vstack.getFrameSlot(1))
        assertEquals(5, vstack.depth())
        assertEquals(0, cstack.labelsDepth())
    }
}
