package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.fixture.module.globalIndex
import io.github.charlietap.chasm.fixture.module.localIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class VariableInstructionExecutorImplTest {

    @Test
    fun `local get instruction delegates to local get executor`() {

        val store = store()
        val stack = stack()

        val instruction = VariableInstruction.LocalGet(localIndex())

        val localGetExecutor: LocalGetExecutor = { passedStack, passedInstruction ->
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)

            Ok(Unit)
        }

        val actual = VariableInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            store = store,
            localGetExecutor = localGetExecutor,
            localSetExecutor = localSetExecutor(),
            localTeeExecutor = localTeeExecutor(),
            globalGetExecutor = globalGetExecutor(),
            globalSetExecutor = globalSetExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `local set instruction delegates to local set executor`() {

        val store = store()
        val stack = stack()

        val instruction = VariableInstruction.LocalSet(localIndex())

        val localSetExecutor: LocalSetExecutor = { passedStack, passedInstruction ->
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)

            Ok(Unit)
        }

        val actual = VariableInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            store = store,
            localGetExecutor = localGetExecutor(),
            localSetExecutor = localSetExecutor,
            localTeeExecutor = localTeeExecutor(),
            globalGetExecutor = globalGetExecutor(),
            globalSetExecutor = globalSetExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `local tee instruction delegates to local tee executor`() {

        val store = store()
        val stack = stack()

        val instruction = VariableInstruction.LocalTee(localIndex())

        val localTeeExecutor: LocalTeeExecutor = { passedStack, passedInstruction ->
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)

            Ok(Unit)
        }

        val actual = VariableInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            store = store,
            localGetExecutor = localGetExecutor(),
            localSetExecutor = localSetExecutor(),
            localTeeExecutor = localTeeExecutor,
            globalGetExecutor = globalGetExecutor(),
            globalSetExecutor = globalSetExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `global get instruction delegates to global get executor`() {

        val store = store()
        val stack = stack()

        val instruction = VariableInstruction.GlobalGet(globalIndex())

        val globalGetExecutor: GlobalGetExecutor = { passedStore, passedStack, passedInstruction ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)

            Ok(Unit)
        }

        val actual = VariableInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            store = store,
            localGetExecutor = localGetExecutor(),
            localSetExecutor = localSetExecutor(),
            localTeeExecutor = localTeeExecutor(),
            globalGetExecutor = globalGetExecutor,
            globalSetExecutor = globalSetExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `global set instruction delegates to global set executor`() {

        val store = store()
        val stack = stack()

        val instruction = VariableInstruction.GlobalSet(globalIndex())

        val globalSetExecutor: GlobalSetExecutor = { passedStore, passedStack, passedInstruction ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)

            Ok(Unit)
        }

        val actual = VariableInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            store = store,
            localGetExecutor = localGetExecutor(),
            localSetExecutor = localSetExecutor(),
            localTeeExecutor = localTeeExecutor(),
            globalGetExecutor = globalGetExecutor(),
            globalSetExecutor = globalSetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun localGetExecutor(): LocalGetExecutor = { _, _ ->
            fail()
        }

        fun localSetExecutor(): LocalSetExecutor = { _, _ ->
            fail()
        }

        fun localTeeExecutor(): LocalTeeExecutor = { _, _ ->
            fail()
        }

        fun globalGetExecutor(): GlobalGetExecutor = { _, _, _ ->
            fail()
        }

        fun globalSetExecutor(): GlobalSetExecutor = { _, _, _ ->
            fail()
        }
    }
}
