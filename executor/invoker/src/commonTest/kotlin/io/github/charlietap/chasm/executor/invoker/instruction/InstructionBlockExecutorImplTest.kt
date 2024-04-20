package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.flow.BreakException
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.label
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstructionBlockExecutorImplTest {

    @Test
    fun `can execute an instruction block return a result`() {

        val store = store()
        val stack = stack()

        val label = label()

        val instructions = listOf(
            NumericInstruction.I32GeS,
            NumericInstruction.I32GeU,
        )
        val params = listOf(
            NumberValue.I32(1),
            NumberValue.I32(2),
        )

        val instructionIter = instructions.iterator()
        val paramsIter = params.asReversed().iterator()

        val instructionExecutor: InstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instructionIter.next(), passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(label, stack.peekLabelOrNull())
            assertEquals(paramsIter.next(), stack.popValueOrNull()?.value)

            Ok(Unit)
        }

        val actual = InstructionBlockExecutorImpl(
            store = store,
            stack = stack,
            label = label,
            instructions = instructions,
            params = params,
            instructionExecutor = instructionExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.labelsDepth())
    }

    @Test
    fun `can catch a break exception then clean up the stack and rethrow it`() {
        val store = store()
        val stack = stack()

        val label = label()

        val instructions = listOf(
            NumericInstruction.I32GeS,
        )
        val params = listOf(
            NumberValue.I32(1),
        )

        val instructionIter = instructions.iterator()
        val paramsIter = params.asReversed().iterator()

        val instructionExecutor: InstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instructionIter.next(), passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(label, stack.peekLabelOrNull())
            assertEquals(paramsIter.next(), stack.peekValueOrNull()?.value)

            repeat(3) { _ ->
                stack.push(value())
            }

            throw BreakException(1, emptyList(), emptyList())
        }

        val didRethrowBreak = try {
            InstructionBlockExecutorImpl(
                store = store,
                stack = stack,
                label = label,
                instructions = instructions,
                params = params,
                instructionExecutor = instructionExecutor,
            )
            false
        } catch (exception: BreakException) {
            val expected = BreakException(0, emptyList(), emptyList())
            assertEquals(expected, exception)
            true
        }

        assertTrue(didRethrowBreak)
        assertEquals(0, stack.labelsDepth())
        assertEquals(0, stack.valuesDepth())
    }

    @Test
    fun `can catch a break exception then clean up the stack and execute the label continuation`() {
        val store = store()
        val stack = stack()

        val label = label()

        val instructions = listOf(
            NumericInstruction.I32GeS,
        )
        val continuation = listOf(
            NumericInstruction.I32LeS,
        )
        val params = listOf(
            NumberValue.I32(118),
        )
        val results = listOf(
            NumberValue.I32(117),
        )

        val instructionIter = (instructions + continuation).iterator()
        val paramsIter = (params + results).iterator()

        var hasManipulatedStack = false
        val instructionExecutor: InstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instructionIter.next(), passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(paramsIter.next(), stack.popValueOrNull()?.value)

            if (!hasManipulatedStack) {
                repeat(3) { _ ->
                    stack.push(value())
                }
                hasManipulatedStack = true
                throw BreakException(0, results, continuation)
            }
            Ok(Unit) // result of continuation
        }

        val actual = InstructionBlockExecutorImpl(
            store = store,
            stack = stack,
            label = label,
            instructions = instructions,
            params = params,
            instructionExecutor = instructionExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.labelsDepth())
        assertEquals(0, stack.valuesDepth())
    }
}
