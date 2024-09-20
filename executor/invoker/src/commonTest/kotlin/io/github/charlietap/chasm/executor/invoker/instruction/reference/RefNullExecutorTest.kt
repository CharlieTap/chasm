package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class RefNullExecutorTest {

    @Test
    fun `can execute the refnull instruction and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val heapType = heapType()

        val instruction = ReferenceInstruction.RefNull(heapType)

        val expected = value(ReferenceValue.Null(heapType))

        val actual = RefNullExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
