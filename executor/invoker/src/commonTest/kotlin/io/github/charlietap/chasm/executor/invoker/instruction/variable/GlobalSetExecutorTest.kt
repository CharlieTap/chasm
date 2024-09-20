package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.instance.globalAddress
import io.github.charlietap.chasm.fixture.instance.globalInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.globalIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalSetExecutorTest {

    @Test
    fun `can execute a global set instruction`() {

        val executionValue = i32(0)
        val globalIndex = globalIndex()
        val globalInstance = globalInstance(
            value = executionValue,
        )
        val globalAddress = globalAddress()
        val moduleInstance = moduleInstance(
            globalAddresses = mutableListOf(globalAddress),
        )
        val store = store(
            globals = mutableListOf(
                globalInstance,
            ),
        )
        val stack = stack()
        val context = executionContext(stack, store)

        val frame = frame(
            state = Stack.Entry.ActivationFrame.State(
                locals = mutableListOf(),
                module = moduleInstance,
            ),
        )

        stack.push(frame)

        val expected = i32(117)
        stack.push(value(expected))

        val instruction = VariableInstruction.GlobalSet(globalIndex)

        val actual = GlobalSetExecutor(
            context = context,
            instruction = instruction,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
        assertEquals(0, stack.valuesDepth())
        assertEquals(expected, store.globals[globalAddress.address].value)
    }
}
