package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class EndFunctionInstructionExecutorTest {

    @Test
    fun `can preserve strict interface results for frame slot functions`() {
        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = store,
        )

        vstack.push(99L)
        vstack.framePointer = 1
        vstack.reserveFrame(2)
        vstack.setFrameSlot(0, 11L)
        vstack.setFrameSlot(1, 22L)

        cstack.push(
            frame(
                arity = 2,
                depths = stackDepths(values = 1),
                previousFramePointer = 0,
                frameSlotMode = true,
            ),
        )

        EndFunctionInstructionExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = AdminInstruction.EndFunction,
        )

        assertEquals(0, vstack.framePointer)
        assertEquals(3, vstack.depth())
        assertEquals(22L, vstack.pop())
        assertEquals(11L, vstack.pop())
        assertEquals(99L, vstack.pop())
    }

    @Test
    fun `can preserve strict frame slot results in the visible caller region`() {
        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = store,
        )

        vstack.reserveFrame(3)
        vstack.setFrameSlot(0, 99L)
        vstack.framePointer = 3
        vstack.reserveFrame(2)
        vstack.setFrameSlot(0, 11L)
        vstack.setFrameSlot(1, 22L)

        cstack.push(
            frame(
                arity = 2,
                depths = stackDepths(values = 3),
                previousFramePointer = 0,
                frameSlotMode = true,
                visibleResultBase = 1,
            ),
        )

        EndFunctionInstructionExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = AdminInstruction.EndFunction,
        )

        assertEquals(0, vstack.framePointer)
        assertEquals(3, vstack.depth())
        assertEquals(99L, vstack.getFrameSlot(0))
        assertEquals(11L, vstack.getFrameSlot(1))
        assertEquals(22L, vstack.getFrameSlot(2))
    }

    @Test
    fun `can preserve strict frame slot results when the caller region is already shared`() {
        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = store,
        )

        vstack.reserveFrame(3)
        vstack.setFrameSlot(0, 99L)
        vstack.framePointer = 2
        vstack.reserveFrame(4)
        vstack.setFrameSlot(0, 11L)

        cstack.push(
            frame(
                arity = 1,
                depths = stackDepths(values = 3),
                previousFramePointer = 0,
                frameSlotMode = true,
                visibleResultBase = 2,
            ),
        )

        EndFunctionInstructionExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = AdminInstruction.EndFunction,
        )

        assertEquals(0, vstack.framePointer)
        assertEquals(3, vstack.depth())
        assertEquals(99L, vstack.getFrameSlot(0))
        assertEquals(11L, vstack.getFrameSlot(2))
    }

    @Test
    fun `unwinds control state before leaving the function`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(
                frame(
                    arity = 0,
                    depths = stackDepths(),
                ),
            ),
            handlers = listOf(
                ExceptionHandler(
                    instructions = emptyList(),
                    framesDepth = 0,
                    instructionsDepth = 0,
                    labelsDepth = 0,
                    framePointer = 0,
                ),
            ),
            instructions = listOf(DispatchableInstruction { _, _, _, _ -> }),
            labels = listOf(label()),
        )
        val vstack = vstack()
        val context = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = store,
        )

        EndFunctionInstructionExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = AdminInstruction.EndFunction,
        )

        assertEquals(0, cstack.handlersDepth())
        assertEquals(0, cstack.instructionsDepth())
        assertEquals(0, cstack.labelsDepth())
    }
}
