package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExceptionalFrameUnwindTest {

    @Test
    fun `unwinding restores the caller and excludes callee results from live depth`() {
        val stack = ValueStack(32)
        stack.activateFrame(4, 8)
        stack.setFrameSlot(0, 42L)
        stack.writeActivationHeader(12, 3, activationHeader(91, 8))
        stack.activateFrame(12, 6)
        stack.setFrameSlot(0, 99L)

        assertEquals(91, stack.unwindCallerFrame(3))
        assertEquals(4, stack.fp)
        assertEquals(12, stack.sp)
        assertEquals(42L, stack.getFrameSlot(0))
    }

    @Test
    fun `unwinding preserves the encoded return field for caller site decoding`() {
        val stack = ValueStack(16)
        val returnIp = (1 shl 30) or 27
        stack.writeActivationHeader(8, 1, activationHeader(returnIp, 8))
        stack.activateFrame(8, 4)

        assertEquals(returnIp, stack.unwindCallerFrame(1))
        assertEquals(0, stack.fp)
        assertEquals(8, stack.sp)
    }

    @Test
    fun `root unwind returns the exit sentinel without decoding it as a call site`() {
        val stack = ValueStack(8)
        stack.writeRootActivationHeader(2)
        stack.activateFrame(0, 5)

        assertEquals(EXIT_IP, stack.unwindCallerFrame(2))
        assertEquals(0, stack.fp)
        assertEquals(0, stack.sp)
    }

    @Test
    fun `invalid frame links trap before changing frame pointers`() {
        for ((returnIp, delta) in listOf(91 to 0, 91 to 9, EXIT_IP to 0, EXIT_IP to 8)) {
            val stack = ValueStack(16)
            stack.writeActivationHeader(8, 1, activationHeader(returnIp, delta))
            stack.activateFrame(8, 4)

            val error = assertFailsWith<InvocationException> { stack.unwindCallerFrame(1) }

            assertEquals(InvocationError.ProgramFinishedInconsistentState, error.error)
            assertEquals(8, stack.fp)
            assertEquals(12, stack.sp)
        }
    }

    @Test
    fun `out of bounds activation headers trap before changing frame pointers`() {
        for (headerSlot in listOf(-1, Int.MAX_VALUE)) {
            val stack = ValueStack(8)
            stack.activateFrame(0, 5)

            val error = assertFailsWith<InvocationException> { stack.unwindCallerFrame(headerSlot) }

            assertEquals(InvocationError.ProgramFinishedInconsistentState, error.error)
            assertEquals(0, stack.fp)
            assertEquals(5, stack.sp)
        }
    }
}
