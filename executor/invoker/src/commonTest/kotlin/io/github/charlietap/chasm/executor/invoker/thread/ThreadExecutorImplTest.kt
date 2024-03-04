package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadExecutorImplTest {

    @Test
    fun `can execute a thread and return a result`() {

        val locals = mutableListOf<ExecutionValue>(NumberValue.I32(2), NumberValue.I32(3))

        val frame = Stack.Entry.ActivationFrame(
            Arity(1),
            Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = moduleInstance(),
            ),
        )

        val thread = Thread(
            frame = frame,
            instructions = listOf(NumericInstruction.I32Mul, NumericInstruction.I32Eqz),
        )

        val configuration = Configuration(
            store = store(),
            thread = thread,
        )

        val instructions = thread.instructions.asSequence().iterator()

        val stack1 = Stack(
            sequenceOf(
                frame,
                Stack.Entry.Value(locals[0]),
                Stack.Entry.Value(locals[1]),
            ),
        )
        val stack2 = Stack(
            sequenceOf(
                frame,
                Stack.Entry.Value(NumberValue.I32(6)),
            ),
        )
        val stack3 = Stack(
            sequenceOf(
                frame,
                Stack.Entry.Value(NumberValue.I32(0)),
            ),
        )
        val inputStacks = sequenceOf(stack1, stack2).iterator()
        val outputStacks = sequenceOf(stack2, stack3).iterator()

        val instructionExecutor: InstructionExecutor = { instruction, store, stack ->
            val inputStack = inputStacks.next()
            val outputStack = outputStacks.next()

            assertEquals(instructions.next(), instruction)
            assertEquals(configuration.store, store)
            assertEquals(inputStack, stack)

            stack.empty()
            stack.fill(outputStack)

            Ok(Unit)
        }

        val actual = ThreadExecutorImpl(configuration, instructionExecutor)

        assertEquals(Ok(listOf(NumberValue.I32(0))), actual)
    }

    @Test
    fun `can execute a thread for a side effect`() {

        val locals = mutableListOf<ExecutionValue>()

        val frame = Stack.Entry.ActivationFrame(
            Arity.SIDE_EFFECT,
            Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = moduleInstance(),
            ),
        )

        val sideEffectInstruction = TableInstruction.ElemDrop(Index.ElementIndex(0u))
        val thread = Thread(
            frame = frame,
            instructions = listOf(sideEffectInstruction),
        )

        val configuration = Configuration(
            store = store(),
            thread = thread,
        )

        val stack = Stack(
            sequenceOf(
                frame,
            ),
        )

        val instructionExecutor: InstructionExecutor = { iInstruction, iStore, iStack ->
            assertEquals(sideEffectInstruction, iInstruction)
            assertEquals(configuration.store, iStore)
            assertEquals(stack, iStack)

            Ok(Unit)
        }

        val actual = ThreadExecutorImpl(configuration, instructionExecutor)

        assertEquals(Ok(emptyList()), actual)
    }
}
