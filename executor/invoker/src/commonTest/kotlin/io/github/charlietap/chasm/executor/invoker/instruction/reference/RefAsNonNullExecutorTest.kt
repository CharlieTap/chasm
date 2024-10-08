package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instruction.refAsNonNullInstruction
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class RefAsNonNullExecutorTest {

    @Test
    fun `can execute the refasnonnull instruction when a function address is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)
        val value = ReferenceValue.Function(functionAddress())

        stack.push(value(value))

        val actual = RefAsNonNullExecutor(
            context = context,
            instruction = refAsNonNullInstruction(),
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(value, stack.popValueOrNull()?.value)
    }

    @Test
    fun `throw a trap when executing refasnonnull when refnull is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)

        stack.push(value(ReferenceValue.Null(heapType())))

        val actual = RefAsNonNullExecutor(
            context = context,
            instruction = refAsNonNullInstruction(),
        )

        assertEquals(Err(InvocationError.Trap.TrapEncountered), actual)
    }
}
