package io.github.charlietap.chasm.executor.invoker.instruction.admin

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.fixture.runtime.configuration
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.thread
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class JumpInstructionExecutorsTest {

    @Test
    fun `can execute a taken jump if and finish consistently`() {
        val endFunction = EndFunctionDispatcher(AdminInstruction.EndFunction)
        val body = arrayOf(
            endFunction,
            CopySlotsDispatcher(
                AdminInstruction.CopySlots(
                    sourceSlots = listOf(1),
                    destinationSlots = listOf(0),
                ),
            ),
            JumpDispatcher(
                AdminInstruction.JumpIfS(
                    operandSlot = 2,
                    continuation = arrayOf(endFunction),
                    discardCount = 2,
                    takenInstructions = listOf(
                        CopySlotsDispatcher(
                            AdminInstruction.CopySlots(
                                sourceSlots = listOf(1),
                                destinationSlots = listOf(0),
                            ),
                        ),
                    ),
                ),
            ),
            dispatchableInstruction { vstack, _, _, _ ->
                vstack.reserveFrame(3)
                vstack.setFrameSlot(1, 1L)
                vstack.setFrameSlot(2, 2L)
                Ok(Unit)
            },
        )

        val actual = ThreadExecutor(
            configuration = configuration(
                store = store(),
                thread = thread(
                    frame = frame(
                        arity = 1,
                        depths = stackDepths(instructions = 1, values = 0),
                        instance = moduleInstance(),
                        frameSlotMode = true,
                    ),
                    instructions = body,
                ),
            ),
            params = emptyList(),
        )

        assertEquals(Ok(listOf(1L)), actual)
    }

    @Test
    fun `can execute a taken jump if with root result copy`() {
        val endFunction = EndFunctionDispatcher(AdminInstruction.EndFunction)
        val rootCopy = CopySlotsDispatcher(
            AdminInstruction.CopySlots(
                sourceSlots = listOf(1),
                destinationSlots = listOf(0),
            ),
        )
        val fallthroughCopy = CopySlotsDispatcher(
            AdminInstruction.CopySlots(
                sourceSlots = listOf(2),
                destinationSlots = listOf(1),
            ),
        )
        val body = arrayOf(
            endFunction,
            rootCopy,
            fallthroughCopy,
            JumpDispatcher(
                AdminInstruction.JumpIfI(
                    operand = 1L,
                    continuation = arrayOf(endFunction, rootCopy),
                    discardCount = 3,
                    takenInstructions = listOf(
                        CopySlotsDispatcher(
                            AdminInstruction.CopySlots(
                                sourceSlots = listOf(2),
                                destinationSlots = listOf(1),
                            ),
                        ),
                    ),
                ),
            ),
            dispatchableInstruction { vstack, _, _, _ ->
                vstack.reserveFrame(3)
                vstack.setFrameSlot(2, 1L)
                Ok(Unit)
            },
        )

        val actual = ThreadExecutor(
            configuration = configuration(
                store = store(),
                thread = thread(
                    frame = frame(
                        arity = 1,
                        depths = stackDepths(instructions = 1, values = 0),
                        instance = moduleInstance(),
                        frameSlotMode = true,
                    ),
                    instructions = body,
                ),
            ),
            params = emptyList(),
        )

        assertEquals(Ok(listOf(1L)), actual)
    }
}
