package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(UnsafeHostApi::class)
class ValueStackLimitTest {

    @Test
    fun `growth fills a non power of two limit exactly and then traps`() {
        val stack = ValueStack(maxCapacity = 100)

        stack.ensureCapacity(100)

        assertEquals(100, stack.unsafeElements().size)
        assertExhausted { stack.ensureCapacity(101) }
    }

    @Test
    fun `pushing past the limit traps without exceeding it`() {
        val stack = ValueStack(maxCapacity = 40)

        repeat(40) { value -> stack.push(value.toLong()) }

        assertEquals(40, stack.unsafeElements().size)
        assertExhausted { stack.push(0L) }
        assertEquals(40, stack.sp)
    }

    @Test
    fun `a limit below the minimum capacity bounds the initial allocation`() {
        val stack = ValueStack(maxCapacity = 3)

        assertEquals(3, stack.unsafeElements().size)
        repeat(3) { value -> stack.push(value.toLong()) }
        assertExhausted { stack.push(0L) }
    }

    @Test
    fun `a zero limit admits an empty stack only`() {
        val stack = ValueStack(maxCapacity = 0)

        assertEquals(0, stack.unsafeElements().size)
        assertExhausted { stack.push(0L) }
    }

    @Test
    fun `an initial frame larger than the limit traps`() {
        assertExhausted { ValueStack(minCapacity = 65, maxCapacity = 64) }
    }

    @Test
    fun `linked frame activation past the limit traps and leaves the caller active`() {
        val stack = ValueStack(minCapacity = 4, maxCapacity = 48)
        stack.activateFrame(fp = 0, frameSlots = 4)

        stack.activateLinkedFrame(
            callFrameOffset = 4,
            frameEndOffset = 48,
            activationHeaderSlot = 0,
            activationHeader = activationHeader(1, callerFrameDelta = 4),
        )
        assertEquals(48, stack.sp)

        assertExhausted {
            stack.activateLinkedFrame(
                callFrameOffset = 4,
                frameEndOffset = 45,
                activationHeaderSlot = 0,
                activationHeader = activationHeader(2, callerFrameDelta = 4),
            )
        }
        assertEquals(4, stack.fp)
        assertTrue(stack.unsafeElements().size <= 48)
    }

    @Test
    fun `byte limits convert to whole slots and never exceed the ceiling`() {
        val ceiling = ValueStack.maxCapacityFor(null)

        assertEquals(1 shl 25, ceiling)
        assertEquals(0, ValueStack.maxCapacityFor(7))
        assertEquals(12, ValueStack.maxCapacityFor(100))
        assertEquals(1 shl 21, ValueStack.maxCapacityFor(16L * 1024 * 1024))
        assertEquals(ceiling, ValueStack.maxCapacityFor(Long.MAX_VALUE))
    }

    private fun assertExhausted(block: () -> Unit) {
        val exception = assertFailsWith<InvocationException> { block() }
        assertEquals(InvocationError.CallStackExhausted, exception.error)
    }
}
