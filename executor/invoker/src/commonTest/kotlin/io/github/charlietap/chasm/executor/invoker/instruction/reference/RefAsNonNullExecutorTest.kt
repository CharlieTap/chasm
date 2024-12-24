package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.refAsNonNullRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class RefAsNonNullExecutorTest {

    @Test
    fun `can execute the refasnonnull instruction when a function address is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)
        val value = functionReferenceValue(functionAddress())

        stack.push(value)

        val actual = RefAsNonNullExecutor(
            context = context,
            instruction = refAsNonNullRuntimeInstruction(),
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(value, stack.popValueOrNull())
    }

    @Test
    fun `throw a trap when executing refasnonnull when refnull is on top of the stack`() {

        val stack = stack()
        val context = executionContext(stack)

        stack.push(nullReferenceValue(heapType()))

        val actual = RefAsNonNullExecutor(
            context = context,
            instruction = refAsNonNullRuntimeInstruction(),
        )

        assertEquals(Err(InvocationError.Trap.TrapEncountered), actual)
    }
}
