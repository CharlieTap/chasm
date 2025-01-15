package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.configuration
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.thread
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadExecutorTest {

    @Test
    fun `can execute a thread and return a result`() {

        val locals = mutableListOf<ExecutionValue>(i32(2), i32(3))

        val frame = frame(
            arity = 1,
            locals = locals,
            instance = moduleInstance(),
        )

        val instructionDispatchable = dispatchableInstruction { context ->
            assertEquals(2, context.stack.valuesDepth())
            assertEquals(1, context.stack.instructionsDepth())
            context.stack.clearValues()
            context.stack.push(i32(0))
            Ok(Unit)
        }
        val thread = thread(
            frame = frame,
            instructions = arrayOf(
                instructionDispatchable,
            ),
        )

        val configuration = configuration(
            store = store(),
            thread = thread,
        )

        val frameDispatchable = dispatchableInstruction { context ->
            context.stack.popFrame()
            assertEquals(0, context.stack.instructionsDepth())
            Ok(Unit)
        }

        val actual = ThreadExecutor(
            configuration = configuration,
            frameCleaner = frameDispatchable,
        )

        assertEquals(Ok(listOf(i32(0))), actual)
    }
}
