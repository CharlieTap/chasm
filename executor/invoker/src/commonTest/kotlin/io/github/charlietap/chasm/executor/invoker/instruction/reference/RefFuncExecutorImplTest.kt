package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.functionIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class RefFuncExecutorImplTest {

    @Test
    fun `can execute the reffunc instruction and return a correct result`() {

        val stack = stack()
        val functionIndex = functionIndex(0u)
        val functionAddress = functionAddress()

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    functionAddresses = mutableListOf(functionAddress()),
                ),
            ),
        )

        stack.push(frame)

        val instruction = ReferenceInstruction.RefFunc(functionIndex)

        val expected = value(ReferenceValue.FunctionAddress(functionAddress))

        val actual = RefFuncExecutorImpl(stack, instruction)

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(expected, stack.popValue())
    }
}
