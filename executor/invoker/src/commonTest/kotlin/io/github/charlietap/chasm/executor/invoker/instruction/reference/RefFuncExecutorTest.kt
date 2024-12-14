package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.refFuncRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.value
import kotlin.test.Test
import kotlin.test.assertEquals

class RefFuncExecutorTest {

    @Test
    fun `can execute the reffunc instruction and return a correct result`() {

        val stack = stack()
        val context = executionContext(stack)
        val functionIndex = functionIndex(0u)
        val functionAddress = functionAddress()

        val frame = frame(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress()),
            ),
        )

        stack.push(frame)

        val instruction = refFuncRuntimeInstruction(functionIndex)

        val expected = value(ReferenceValue.Function(functionAddress))

        val actual = RefFuncExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValueOrNull())
    }
}
