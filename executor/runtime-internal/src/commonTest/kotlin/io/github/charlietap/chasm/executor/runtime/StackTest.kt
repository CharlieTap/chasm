package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.fixture.executor.runtime.label
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class StackTest {

    @Test
    fun `peek nth frame returns the correct value`() {

        val stack = stack()

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

        val stack = stack()

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

        val stack = stack()

        val value1 = i32(1)

        val value2 = i32(2)

        stack.push(value1)
        stack.push(value2)

        val result1 = stack.peekNthValue(0)
        val result2 = stack.peekNthValue(1)

        assertEquals(value2, result1)
        assertEquals(value1, result2)
    }

    @Test
    fun `squash n returns the correct value`() {

        val stack = stack()

        val value1 = i32(0)
        val value2 = i32(1)
        val value3 = i32(2)
        val value4 = i32(3)
        val value5 = i32(4)

        stack.push(value1)
        stack.push(value2)
        stack.push(value3)
        stack.push(value4)
        stack.push(value5)

        stack.shrinkValues(2, 1)

        val expected = listOf(value1, value4, value5)

        assertEquals(expected, stack.values())
    }
}
