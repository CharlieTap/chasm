package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.instruction.dropInstruction
import io.github.charlietap.chasm.fixture.instruction.selectInstruction
import io.github.charlietap.chasm.fixture.instruction.selectWithTypeInstruction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ParametricInstructionExecutorTest {

    @Test
    fun `drop instruction delegates to drop executor`() {

        val context = executionContext()

        val instruction = dropInstruction()

        val dropExecutor: Executor<ParametricInstruction.Drop> = { _, _ ->
            Ok(Unit)
        }

        val actual = parametricInstructionExecutor(
            context = context,
            instruction = instruction,
            dropExecutor = dropExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `select instruction delegates to select executor`() {

        val context = executionContext()

        val instruction = selectInstruction()

        val selectExecutor: Executor<ParametricInstruction.Select> = { _, _ ->
            Ok(Unit)
        }

        val actual = parametricInstructionExecutor(
            context = context,
            instruction = instruction,
            selectExecutor = selectExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `select with type instruction delegates to select executor`() {

        val context = executionContext()

        val instruction = selectWithTypeInstruction()

        val selectWithTypeExecutor: Executor<ParametricInstruction.SelectWithType> = { _, _ ->
            Ok(Unit)
        }

        val actual = parametricInstructionExecutor(
            context = context,
            instruction = instruction,
            selectWithTypeExecutor = selectWithTypeExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun dropExecutor(): Executor<ParametricInstruction.Drop> = { _, _ ->
            fail()
        }

        fun selectExecutor(): Executor<ParametricInstruction.Select> = { _, _ ->
            fail()
        }

        fun selectWithTypeExecutor(): Executor<ParametricInstruction.SelectWithType> = { _, _ ->
            fail()
        }

        fun parametricInstructionExecutor(
            context: ExecutionContext,
            instruction: ParametricInstruction,
            dropExecutor: Executor<ParametricInstruction.Drop> = dropExecutor(),
            selectExecutor: Executor<ParametricInstruction.Select> = selectExecutor(),
            selectWithTypeExecutor: Executor<ParametricInstruction.SelectWithType> = selectWithTypeExecutor(),
        ) = ParametricInstructionExecutor(
            context = context,
            instruction = instruction,
            dropExecutor = dropExecutor,
            selectExecutor = selectExecutor,
            selectWithTypeExecutor = selectWithTypeExecutor,
        )
    }
}
