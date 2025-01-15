package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.callRefRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.functionReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class CallRefExecutorTest {

    @Test
    fun `can execute a callref instruction to a wasm function and return a result`() {

        val stack = stack()
        val functionInstruction = dispatchableInstruction()
        val store = store(
            instructions = mutableListOf(functionInstruction),
        )
        val context = executionContext(stack, store)

        val functionAddress = functionAddress(0)
        stack.push(functionReferenceValue(functionAddress))

        val actual = CallRefExecutor(context, callRefRuntimeInstruction())

        assertEquals(Unit, actual)
    }
}
