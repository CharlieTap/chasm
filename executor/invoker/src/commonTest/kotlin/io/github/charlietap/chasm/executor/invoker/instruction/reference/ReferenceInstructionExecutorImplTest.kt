package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.fixture.instruction.functionIndex
import io.github.charlietap.chasm.fixture.type.heapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ReferenceInstructionExecutorImplTest {

    @Test
    fun `delegate the refnull instruction to the refNullExecutor`() {

        val stack = Stack()

        val instruction = ReferenceInstruction.RefNull(heapType())

        val refNullExecutor: RefNullExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ReferenceInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            refNullExecutor = refNullExecutor,
            refIsNullExecutor = refIsNullExecutor(),
            refFuncExecutor = refFuncExecutor(),
            refAsNonNullExecutor = refAsNonNullExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the refisnull instruction to the refIsNullExecutor`() {

        val stack = Stack()

        val instruction = ReferenceInstruction.RefIsNull

        val refIsNullExecutor: RefIsNullExecutor = { _stack ->
            assertEquals(stack, _stack)
            Ok(Unit)
        }

        val actual = ReferenceInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            refNullExecutor = refNullExecutor(),
            refIsNullExecutor = refIsNullExecutor,
            refFuncExecutor = refFuncExecutor(),
            refAsNonNullExecutor = refAsNonNullExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the reffunc instruction to the refFuncExecutor`() {

        val stack = Stack()

        val instruction = ReferenceInstruction.RefFunc(functionIndex())

        val refFuncExecutor: RefFuncExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ReferenceInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            refNullExecutor = refNullExecutor(),
            refIsNullExecutor = refIsNullExecutor(),
            refFuncExecutor = refFuncExecutor,
            refAsNonNullExecutor = refAsNonNullExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the refasnonnull instruction to the refAsNonNullExecutor`() {

        val stack = Stack()

        val instruction = ReferenceInstruction.RefAsNonNull

        val refAsNonNullExecutor: RefAsNonNullExecutor = { _stack ->
            assertEquals(stack, _stack)
            Ok(Unit)
        }

        val actual = ReferenceInstructionExecutorImpl(
            instruction = instruction,
            stack = stack,
            refNullExecutor = refNullExecutor(),
            refIsNullExecutor = refIsNullExecutor(),
            refFuncExecutor = refFuncExecutor(),
            refAsNonNullExecutor = refAsNonNullExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun refNullExecutor(): RefNullExecutor = { _, _ ->
            fail("RefNullExecutor should not be called in this scenario")
        }

        fun refIsNullExecutor(): RefIsNullExecutor = { _ ->
            fail("RefIsNullExecutor should not be called in this scenario")
        }

        fun refFuncExecutor(): RefFuncExecutor = { _, _ ->
            fail("RefFuncExecutor should not be called in this scenario")
        }

        fun refAsNonNullExecutor(): RefAsNonNullExecutor = { _ ->
            fail("RefAsNonNullExecutor should not be called in this scenario")
        }
    }
}
