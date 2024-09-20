package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.instruction.refAsNonNullInstruction
import io.github.charlietap.chasm.fixture.instruction.refEqInstruction
import io.github.charlietap.chasm.fixture.instruction.refFuncInstruction
import io.github.charlietap.chasm.fixture.instruction.refIsNullInstruction
import io.github.charlietap.chasm.fixture.instruction.refNullInstruction
import io.github.charlietap.chasm.fixture.instruction.refTestInstruction
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.referenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ReferenceInstructionExecutorTest {

    @Test
    fun `delegate the refnull instruction to the refNullExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refNullInstruction()

        val refNullExecutor: Executor<ReferenceInstruction.RefNull> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refNullExecutor = refNullExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the refisnull instruction to the refIsNullExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refIsNullInstruction()

        val refIsNullExecutor: Executor<ReferenceInstruction.RefIsNull> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refIsNullExecutor = refIsNullExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the reffunc instruction to the refFuncExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refFuncInstruction()

        val refFuncExecutor: Executor<ReferenceInstruction.RefFunc> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refFuncExecutor = refFuncExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the refasnonnull instruction to the refAsNonNullExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refAsNonNullInstruction()

        val refAsNonNullExecutor: Executor<ReferenceInstruction.RefAsNonNull> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refAsNonNullExecutor = refAsNonNullExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the refeq instruction to the refEqExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refEqInstruction()

        val refEqExecutor: Executor<ReferenceInstruction.RefEq> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refEqExecutor = refEqExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the reftest instruction to the refTestExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refTestInstruction()

        val refTestExecutor: Executor<ReferenceInstruction.RefTest> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refTestExecutor = refTestExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the refcast instruction to the refCastExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val referenceType = referenceType()
        val instruction = ReferenceInstruction.RefCast(referenceType)

        val refCastExecutor: Executor<ReferenceInstruction.RefCast> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = referenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refCastExecutor = refCastExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun refNullExecutor(): Executor<ReferenceInstruction.RefNull> = { _, _ ->
            fail("RefNullExecutor should not be called in this scenario")
        }

        fun refIsNullExecutor(): Executor<ReferenceInstruction.RefIsNull> = { _, _ ->
            fail("RefIsNullExecutor should not be called in this scenario")
        }

        fun refFuncExecutor(): Executor<ReferenceInstruction.RefFunc> = { _, _ ->
            fail("RefFuncExecutor should not be called in this scenario")
        }

        fun refAsNonNullExecutor(): Executor<ReferenceInstruction.RefAsNonNull> = { _, _ ->
            fail("RefAsNonNullExecutor should not be called in this scenario")
        }

        fun refEqExecutor(): Executor<ReferenceInstruction.RefEq> = { _, _ ->
            fail("RefEqExecutor should not be called in this scenario")
        }

        fun refTestExecutor(): Executor<ReferenceInstruction.RefTest> = { _, _ ->
            fail("RefTestExecutor should not be called in this scenario")
        }

        fun refCastExecutor(): Executor<ReferenceInstruction.RefCast> = { _, _ ->
            fail("RefCastExecutor should not be called in this scenario")
        }

        fun referenceInstructionExecutor(
            context: ExecutionContext,
            instruction: ReferenceInstruction,
            refNullExecutor: Executor<ReferenceInstruction.RefNull> = refNullExecutor(),
            refIsNullExecutor: Executor<ReferenceInstruction.RefIsNull> = refIsNullExecutor(),
            refFuncExecutor: Executor<ReferenceInstruction.RefFunc> = refFuncExecutor(),
            refAsNonNullExecutor: Executor<ReferenceInstruction.RefAsNonNull> = refAsNonNullExecutor(),
            refEqExecutor: Executor<ReferenceInstruction.RefEq> = refEqExecutor(),
            refTestExecutor: Executor<ReferenceInstruction.RefTest> = refTestExecutor(),
            refCastExecutor: Executor<ReferenceInstruction.RefCast> = refCastExecutor(),
        ) = ReferenceInstructionExecutor(
            context = context,
            instruction = instruction,
            refNullExecutor = refNullExecutor,
            refIsNullExecutor = refIsNullExecutor,
            refFuncExecutor = refFuncExecutor,
            refAsNonNullExecutor = refAsNonNullExecutor,
            refEqExecutor = refEqExecutor,
            refTestExecutor = refTestExecutor,
            refCastExecutor = refCastExecutor,
        )
    }
}
