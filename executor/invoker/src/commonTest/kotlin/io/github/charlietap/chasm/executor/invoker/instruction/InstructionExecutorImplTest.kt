package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.control.ControlInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.NumericInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.ParametricInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.ReferenceInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variable.VariableInstructionExecutor
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class InstructionExecutorImplTest {

    @Test
    fun `delegates to control instruction executor when given a control instruction`() {

        val store = store()
        val stack = stack()

        val instruction = ControlInstruction.Nop

        val controlInstructionExecutor: ControlInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = InstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            controlInstructionExecutor = controlInstructionExecutor,
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to memory instruction executor when given a memory instruction`() {

        val store = store()
        val stack = stack()

        val instruction = MemoryInstruction.MemoryGrow

        val memoryInstructionExecutor: MemoryInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = InstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor,
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to numeric instruction executor when given a numeric instruction`() {

        val store = store()
        val stack = stack()

        val instruction = NumericInstruction.I32GeS

        val numericInstructionExecutor: NumericInstructionExecutor = { passedInstruction, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = InstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor,
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to parametric instruction executor when given a parametric instruction`() {

        val store = store()
        val stack = stack()

        val instruction = ParametricInstruction.Drop

        val parametricInstructionExecutor: ParametricInstructionExecutor = { passedInstruction, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = InstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor,
            referenceInstructionExecutor = referenceInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to reference instruction executor when given a reference instruction`() {

        val store = store()
        val stack = stack()

        val instruction = ReferenceInstruction.RefIsNull

        val referenceInstructionExecutor: ReferenceInstructionExecutor = { passedInstruction, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = InstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor,
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to variable instruction executor when given a variable instruction`() {

        val store = store()
        val stack = stack()

        val instruction = VariableInstruction.GlobalSet(Index.GlobalIndex(0u))

        val variableInstructionExecutor: VariableInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = InstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun controlInstructionExecutor(): ControlInstructionExecutor = { _, _, _ ->
            fail()
        }

        fun memoryInstructionExecutor(): MemoryInstructionExecutor = { _, _, _ ->
            fail()
        }

        fun numericInstructionExecutor(): NumericInstructionExecutor = { _, _ ->
            fail()
        }

        fun parametricInstructionExecutor(): ParametricInstructionExecutor = { _, _ ->
            fail()
        }

        fun referenceInstructionExecutor(): ReferenceInstructionExecutor = { _, _ ->
            fail()
        }

        fun variableInstructionExecutor(): VariableInstructionExecutor = { _, _, _ ->
            fail()
        }
    }
}
