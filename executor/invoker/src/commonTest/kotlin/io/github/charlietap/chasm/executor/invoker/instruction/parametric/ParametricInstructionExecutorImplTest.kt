package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ParametricInstructionExecutorImplTest {

    @Test
    fun `drop instruction delegates to drop executor`() {

        val stack = Stack()

        val instruction = ParametricInstruction.Drop

        val dropExecutor: DropExecutor = { _ ->
            Ok(Unit)
        }

        val actual = ParametricInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            dropExecutor = dropExecutor,
            selectExecutor = selectExecutor(),
            selectWithTypeExecutor = selectWithTypeExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `select instruction delegates to select executor`() {

        val stack = Stack()

        val instruction = ParametricInstruction.Select

        val selectExecutor: SelectExecutor = { _ ->
            Ok(Unit)
        }

        val actual = ParametricInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            dropExecutor = dropExecutor(),
            selectExecutor = selectExecutor,
            selectWithTypeExecutor = selectWithTypeExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `select with type instruction delegates to select executor`() {

        val stack = Stack()

        val instruction = ParametricInstruction.SelectWithType(emptyList())

        val selectWithTypeExecutor: SelectWithTypeExecutor = { _, _ ->
            Ok(Unit)
        }

        val actual = ParametricInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            dropExecutor = dropExecutor(),
            selectExecutor = selectExecutor(),
            selectWithTypeExecutor = selectWithTypeExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun dropExecutor(): DropExecutor = { _ ->
            fail()
        }

        fun selectExecutor(): SelectExecutor = { _ ->
            fail()
        }

        fun selectWithTypeExecutor(): SelectWithTypeExecutor = { _, _ ->
            fail()
        }
    }
}
