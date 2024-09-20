package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.module.globalIndex
import io.github.charlietap.chasm.fixture.module.localIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class VariableInstructionExecutorTest {

    @Test
    fun `local get instruction delegates to local get executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = VariableInstruction.LocalGet(localIndex())

        val localGetExecutor: Executor<VariableInstruction.LocalGet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = variableInstructionExecutor(
            context = context,
            instruction = instruction,
            localGetExecutor = localGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `local set instruction delegates to local set executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = VariableInstruction.LocalSet(localIndex())

        val localSetExecutor: Executor<VariableInstruction.LocalSet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = variableInstructionExecutor(
            context = context,
            instruction = instruction,
            localSetExecutor = localSetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `local tee instruction delegates to local tee executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = VariableInstruction.LocalTee(localIndex())

        val localTeeExecutor: Executor<VariableInstruction.LocalTee> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = variableInstructionExecutor(
            context = context,
            instruction = instruction,
            localTeeExecutor = localTeeExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `global get instruction delegates to global get executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = VariableInstruction.GlobalGet(globalIndex())

        val globalGetExecutor: Executor<VariableInstruction.GlobalGet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = variableInstructionExecutor(
            context = context,
            instruction = instruction,
            globalGetExecutor = globalGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `global set instruction delegates to global set executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = VariableInstruction.GlobalSet(globalIndex())

        val globalSetExecutor: Executor<VariableInstruction.GlobalSet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = variableInstructionExecutor(
            context = context,
            instruction = instruction,
            globalSetExecutor = globalSetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun localGetExecutor(): Executor<VariableInstruction.LocalGet> = { _, _ ->
            fail()
        }

        fun localSetExecutor(): Executor<VariableInstruction.LocalSet> = { _, _ ->
            fail()
        }

        fun localTeeExecutor(): Executor<VariableInstruction.LocalTee> = { _, _ ->
            fail()
        }

        fun globalGetExecutor(): Executor<VariableInstruction.GlobalGet> = { _, _ ->
            fail()
        }

        fun globalSetExecutor(): Executor<VariableInstruction.GlobalSet> = { _, _ ->
            fail()
        }

        fun variableInstructionExecutor(
            context: ExecutionContext,
            instruction: VariableInstruction,
            localGetExecutor: Executor<VariableInstruction.LocalGet> = localGetExecutor(),
            localSetExecutor: Executor<VariableInstruction.LocalSet> = localSetExecutor(),
            localTeeExecutor: Executor<VariableInstruction.LocalTee> = localTeeExecutor(),
            globalGetExecutor: Executor<VariableInstruction.GlobalGet> = globalGetExecutor(),
            globalSetExecutor: Executor<VariableInstruction.GlobalSet> = globalSetExecutor(),
        ) = VariableInstructionExecutor(
            context = context,
            instruction = instruction,
            localGetExecutor = localGetExecutor,
            localSetExecutor = localSetExecutor,
            localTeeExecutor = localTeeExecutor,
            globalGetExecutor = globalGetExecutor,
            globalSetExecutor = globalSetExecutor,
        )
    }
}
