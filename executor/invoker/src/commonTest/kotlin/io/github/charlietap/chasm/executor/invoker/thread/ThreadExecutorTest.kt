package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.Value
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.ext.popFrame
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.returnArity
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadExecutorTest {

    @Test
    fun `can execute a thread and return a result`() {

        val locals = mutableListOf<ExecutionValue>(i32(2), i32(3))

        val frame = frame(
            arity = returnArity(1),
            state = frameState(
                locals = locals,
                moduleInstance = moduleInstance(),
            ),
        )

        val thread = Thread(
            frame = frame,
            instructions = listOf(NumericInstruction.I32Mul, NumericInstruction.I32Eqz).map(::ModuleInstruction),
        )

        val configuration = Configuration(
            store = store(),
            thread = thread,
        )

        val instructions = (thread.instructions + listOf(AdminInstruction.Frame(frame))).asSequence().iterator()

        val stack1 = stack(
            values = ArrayDeque(
                listOf(
                    Value(locals[0]),
                    Value(locals[1]),
                ),
            ),
        )
        val stack2 = stack(
            values = ArrayDeque(
                listOf(
                    Value(i32(6)),
                ),
            ),
        )
        val stack3 = stack(
            values = ArrayDeque(
                listOf(
                    Value(i32(0)),
                ),
            ),
        )
        val inputStacks = sequenceOf(stack1, stack2, stack3).iterator()
        val outputStacks = sequenceOf(stack2, stack3, stack3).iterator()

        val instructionExecutor: Executor<ExecutionInstruction> = { context, instruction ->

            val inputStack = inputStacks.next()
            val outputStack = outputStacks.next()

            assertEquals(instructions.next(), instruction)
            assertEquals(configuration.store, context.store)
            assertEquals(inputStack.values(), context.stack.values())

            context.stack.values().removeAll { true }
            context.stack.fill(outputStack)

            if (instruction is AdminInstruction.Frame) {
                context.stack.popFrame()
            }

            Ok(Unit)
        }

        val actual = ThreadExecutor(configuration, instructionExecutor)

        assertEquals(Ok(listOf(i32(0))), actual)
    }
}
