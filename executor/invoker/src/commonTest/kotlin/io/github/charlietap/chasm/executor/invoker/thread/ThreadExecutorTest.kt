package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.executor.runtime.configuration
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.thread
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadExecutorTest {

    @Test
    fun `can execute a thread and return a result`() {

        val frame = frame(
            arity = 1,
            instance = moduleInstance(),
        )

        val instructionDispatchable = dispatchableInstruction { context ->
            assertEquals(2, context.vstack.depth())
            assertEquals(2, context.cstack.instructionsDepth())
            context.vstack.clear()
            context.vstack.pushI32(0)
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
            context.cstack.popFrame()
            assertEquals(1, context.cstack.instructionsDepth()) // exit loop instr
            Ok(Unit)
        }
        val params = mutableListOf<ExecutionValue>(i32(2), i32(3))

        val actual = ThreadExecutor(
            configuration = configuration,
            frameCleaner = frameDispatchable,
            params = params,
        )

        assertEquals(Ok(listOf(0L)), actual)
    }
}
