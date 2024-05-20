package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value.i31ReferenceValue
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class I31GetExecutorImplTest {

    @Test
    fun `can execute the I31GetExecutor with signed extension and return a correct result`() {

        val stack = stack()
        val signedExtension = true
        val i32 = 117
        val i31 = i31ReferenceValue()

        stack.push(i31)

        val i31SignedExtender: (ReferenceValue.I31) -> Int = { input ->
            assertEquals(i31, input)
            i32
        }

        val i31UnsignedExtender: (ReferenceValue.I31) -> Int = { _ ->
            fail("Unsigned extender should not be called in this scenario")
        }

        val actual = I31GetExecutorImpl(stack, signedExtension, i31SignedExtender, i31UnsignedExtender)

        assertEquals(Ok(Unit), actual)
        assertEquals(i32(i32), stack.popValueOrNull()?.value)
    }

    @Test
    fun `can execute the I31GetExecutor with unsigned extension and return a correct result`() {

        val stack = stack()
        val signedExtension = false
        val i32 = 117
        val i31 = i31ReferenceValue()

        stack.push(i31)

        val i31SignedExtender: (ReferenceValue.I31) -> Int = { _ ->
            fail("Unsigned extender should not be called in this scenario")
        }

        val i31UnsignedExtender: (ReferenceValue.I31) -> Int = { input ->
            assertEquals(i31, input)
            i32
        }

        val actual = I31GetExecutorImpl(stack, signedExtension, i31SignedExtender, i31UnsignedExtender)

        assertEquals(Ok(Unit), actual)
        assertEquals(i32(i32), stack.popValueOrNull()?.value)
    }
}
