package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.executionValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewExecutorImplTest {

    @Test
    fun `can execute the ArrayNew instruction and return a correct result`() {

        val store = store()
        val stack = stack()
        val size = 2u
        val typeIndex = typeIndex(0u)
        val executionValue = executionValue()

        stack.push(executionValue)

        stack.push(NumberValue.I32(size.toInt()))

        val arrayNewFixedExecutor: ArrayNewFixedExecutor = { _store, _stack, _typeIndex, _size ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(size, _size)

            Ok(Unit)
        }

        val actual = ArrayNewExecutorImpl(store, stack, typeIndex, arrayNewFixedExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(executionValue, stack.popValueOrNull()?.value)
        assertEquals(executionValue, stack.popValueOrNull()?.value)
    }
}
