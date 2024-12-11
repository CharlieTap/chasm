package io.github.charlietap.chasm.executor.invoker.fixture

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.stack
import kotlin.test.Test
import kotlin.test.assertEquals

class StackExtTest {

    @Test
    fun `can push a stack frame to the stack`() {

        val stack = stack()
        val frame = frame()

        val actual = stack.pushFrame(frame)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.size())

        val frameEntry = stack.popFrameOrNull()
        assertEquals(frame, frameEntry)
    }

    @Test
    fun `pushing too many frames to the stack returns an error`() {

        val frame = frame()
        val stack = stack(
            frames = List(Stack.MAX_DEPTH) { frame },
        )

        val actual = stack.pushFrame(frame)

        assertEquals(Err(InvocationError.CallStackExhausted), actual)
        assertEquals(Stack.MAX_DEPTH, stack.size())
    }
}
