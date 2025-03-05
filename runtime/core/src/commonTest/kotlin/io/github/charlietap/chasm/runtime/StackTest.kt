package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import kotlin.test.Test
import kotlin.test.assertEquals

class StackTest {

    @Test
    fun `peek nth frame returns the correct value`() {

        val stack = cstack()

        val frame1 = frame(
            arity = 1,
        )

        val frame2 = frame(
            arity = 2,
        )

        stack.push(frame1)
        stack.push(frame2)

        val result1 = stack.peekNthFrameOrNull(0)
        val result2 = stack.peekNthFrameOrNull(1)

        assertEquals(frame2, result1)
        assertEquals(frame1, result2)
    }

    @Test
    fun `peek nth label returns the correct value`() {

        val stack = cstack()

        val label1 = label(
            arity = 1,
        )

        val label2 = label(
            arity = 2,
        )

        stack.push(label1)
        stack.push(label2)

        val result1 = stack.peekNthLabel(0)
        val result2 = stack.peekNthLabel(1)

        assertEquals(label2, result1)
        assertEquals(label1, result2)
    }

    @Test
    fun `peek nth value returns the correct value`() {

        val stack = vstack()

        val value1 = 1
        val value2 = 2

        stack.pushI32(value1)
        stack.pushI32(value2)

        val result1 = stack.peekNthI32(0)
        val result2 = stack.peekNthI32(1)

        assertEquals(value2, result1)
        assertEquals(value1, result2)
    }

    @Test
    fun `squash n returns the correct value`() {

        val stack = vstack()

        val value1 = 0
        val value2 = 1
        val value3 = 2
        val value4 = 3
        val value5 = 4

        stack.pushI32(value1)
        stack.pushI32(value2)
        stack.pushI32(value3)
        stack.pushI32(value4)
        stack.pushI32(value5)

        stack.shrink(2, 1)

        val expected = listOf(value1, value4, value5)
        val actual = List(stack.depth()) {
            stack.popI32()
        }.asReversed()

        assertEquals(expected, actual)
    }
}
