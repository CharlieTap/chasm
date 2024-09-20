package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
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
        val context = executionContext(stack, store)

        val instruction = AggregateInstruction.ArrayLen

        val aggregateInstructionExecutor: Executor<AggregateInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = ControlInstruction.Nop

        val controlInstructionExecutor: Executor<ControlInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = memoryGrowInstruction()

        val memoryInstructionExecutor: Executor<MemoryInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = NumericInstruction.I32GeS

        val numericInstructionExecutor: Executor<NumericInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = ParametricInstruction.Drop

        val parametricInstructionExecutor: Executor<ParametricInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = ReferenceInstruction.RefIsNull

        val referenceInstructionExecutor: Executor<ReferenceInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = TableInstruction.TableGet(tableIndex())

        val tableInstructionExecutor: Executor<TableInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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
        val context = executionContext(stack, store)

        val instruction = VariableInstruction.GlobalSet(Index.GlobalIndex(0u))

        val variableInstructionExecutor: Executor<VariableInstruction> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = ModuleInstructionExecutor(
            context = context,
            instruction = ModuleInstruction(instruction),
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

        fun aggregateInstructionExecutor(): Executor<AggregateInstruction> = { _, _ ->
            fail("control instruction executor shouldn't be called")
        }

        fun controlInstructionExecutor(): Executor<ControlInstruction> = { _, _ ->
            fail("control instruction executor shouldn't be called")
        }

        fun memoryInstructionExecutor(): Executor<MemoryInstruction> = { _, _ ->
            fail("memory instruction executor shouldn't be called")
        }

        fun numericInstructionExecutor(): Executor<NumericInstruction> = { _, _ ->
            fail("numeric instruction executor shouldn't be called")
        }

        fun parametricInstructionExecutor(): Executor<ParametricInstruction> = { _, _ ->
            fail("parametric instruction executor shouldn't be called")
        }

        fun referenceInstructionExecutor(): Executor<ReferenceInstruction> = { _, _ ->
            fail("reference instruction executor shouldn't be called")
        }

        fun tableInstructionExecutor(): Executor<TableInstruction> = { _, _ ->
            fail("table instruction executor shouldn't be called")
        }

        fun variableInstructionExecutor(): Executor<VariableInstruction> = { _, _ ->
            fail("variable instruction executor shouldn't be called")
        }
    }
}
