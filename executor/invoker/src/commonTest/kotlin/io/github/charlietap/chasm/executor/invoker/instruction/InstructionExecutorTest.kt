package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AggregateInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.ControlInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.MemoryInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.NumericInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.parametric.ParametricInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.ReferenceInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.table.TableInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variable.VariableInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.fixture.instruction.memoryGrowInstruction
import io.github.charlietap.chasm.fixture.module.tableIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class InstructionExecutorTest {

    @Test
    fun `delegates to aggregate instruction executor when given an aggregate instruction`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.ArrayLen

        val aggregateInstructionExecutor: AggregateInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor,
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

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

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor,
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to memory instruction executor when given a memory instruction`() {

        val store = store()
        val stack = stack()

        val instruction = memoryGrowInstruction()

        val memoryInstructionExecutor: MemoryInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor,
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor(),
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

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor,
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor(),
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

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor,
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to reference instruction executor when given a reference instruction`() {

        val store = store()
        val stack = stack()

        val instruction = ReferenceInstruction.RefIsNull

        val referenceInstructionExecutor: ReferenceInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor,
            tableInstructionExecutor = tableInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegates to table instruction executor when given a reference instruction`() {

        val store = store()
        val stack = stack()

        val instruction = TableInstruction.TableGet(tableIndex())

        val tableInstructionExecutor: TableInstructionExecutor = { passedInstruction, passedStore, passedStack ->
            assertEquals(instruction, passedInstruction)
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor,
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

        val actual = ModuleInstructionExecutor(
            instruction = ModuleInstruction(instruction),
            store = store,
            stack = stack,
            aggregateInstructionExecutor = aggregateInstructionExecutor(),
            controlInstructionExecutor = controlInstructionExecutor(),
            memoryInstructionExecutor = memoryInstructionExecutor(),
            numericInstructionExecutor = numericInstructionExecutor(),
            parametricInstructionExecutor = parametricInstructionExecutor(),
            referenceInstructionExecutor = referenceInstructionExecutor(),
            tableInstructionExecutor = tableInstructionExecutor(),
            variableInstructionExecutor = variableInstructionExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {

        fun aggregateInstructionExecutor(): AggregateInstructionExecutor = { _, _, _ ->
            fail("control instruction executor shouldn't be called")
        }

        fun controlInstructionExecutor(): ControlInstructionExecutor = { _, _, _ ->
            fail("control instruction executor shouldn't be called")
        }

        fun memoryInstructionExecutor(): MemoryInstructionExecutor = { _, _, _ ->
            fail("memory instruction executor shouldn't be called")
        }

        fun numericInstructionExecutor(): NumericInstructionExecutor = { _, _ ->
            fail("numeric instruction executor shouldn't be called")
        }

        fun parametricInstructionExecutor(): ParametricInstructionExecutor = { _, _ ->
            fail("parametric instruction executor shouldn't be called")
        }

        fun referenceInstructionExecutor(): ReferenceInstructionExecutor = { _, _, _ ->
            fail("reference instruction executor shouldn't be called")
        }

        fun tableInstructionExecutor(): TableInstructionExecutor = { _, _, _ ->
            fail("table instruction executor shouldn't be called")
        }

        fun variableInstructionExecutor(): VariableInstructionExecutor = { _, _, _ ->
            fail("variable instruction executor shouldn't be called")
        }
    }
}
