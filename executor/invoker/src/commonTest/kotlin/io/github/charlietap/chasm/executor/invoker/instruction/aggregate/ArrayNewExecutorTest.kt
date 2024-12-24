package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewExecutorTest {

    @Test
    fun `can execute the ArrayNew instruction and return a correct result`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val size = 2u
        val typeIndex = typeIndex(0u)
        val executionValue = executionValue()

        stack.pushValue(executionValue)

        stack.pushValue(i32(size.toInt()))

        val arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(AggregateInstruction.ArrayNewFixed(typeIndex, size), _instruction)
            Ok(Unit)
        }

        val actual = ArrayNewExecutor(context, AggregateInstruction.ArrayNew(typeIndex), arrayNewFixedExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(executionValue, stack.popValueOrNull())
        assertEquals(executionValue, stack.popValueOrNull())
    }
}
