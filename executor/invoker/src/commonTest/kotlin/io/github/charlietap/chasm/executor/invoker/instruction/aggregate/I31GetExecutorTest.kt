package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class I31GetExecutorTest {

    @Test
    fun `can execute the I31GetExecutor with signed extension and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val signedExtension = true
        val i32 = 117
        val i31 = i31ReferenceValue()

        stack.pushValue(i31)

        val i31SignedExtender: (ReferenceValue.I31) -> Int = { input ->
            assertEquals(i31, input)
            i32
        }

        val i31UnsignedExtender: (ReferenceValue.I31) -> Int = { _ ->
            fail("Unsigned extender should not be called in this scenario")
        }

        val actual = I31GetExecutor(context, signedExtension, i31SignedExtender, i31UnsignedExtender)

        assertEquals(Unit, actual)
        assertEquals(i32(i32), stack.popValue())
    }

    @Test
    fun `can execute the I31GetExecutor with unsigned extension and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val signedExtension = false
        val i32 = 117
        val i31 = i31ReferenceValue()

        stack.pushValue(i31)

        val i31SignedExtender: (ReferenceValue.I31) -> Int = { _ ->
            fail("Unsigned extender should not be called in this scenario")
        }

        val i31UnsignedExtender: (ReferenceValue.I31) -> Int = { input ->
            assertEquals(i31, input)
            i32
        }

        val actual = I31GetExecutor(context, signedExtension, i31SignedExtender, i31UnsignedExtender)

        assertEquals(Unit, actual)
        assertEquals(i32(i32), stack.popValue())
    }
}
