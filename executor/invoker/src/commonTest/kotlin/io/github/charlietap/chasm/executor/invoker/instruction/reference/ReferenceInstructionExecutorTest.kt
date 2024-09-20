package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.module.functionIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.heapType
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

        val instruction = ReferenceInstruction.RefNull(heapType())

        val refNullExecutor: RefNullExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
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

        val instruction = ReferenceInstruction.RefIsNull

        val refIsNullExecutor: RefIsNullExecutor = { _stack ->
            assertEquals(stack, _stack)
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

        val instruction = ReferenceInstruction.RefFunc(functionIndex())

        val refFuncExecutor: RefFuncExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
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

        val instruction = ReferenceInstruction.RefAsNonNull

        val refAsNonNullExecutor: RefAsNonNullExecutor = { _stack ->
            assertEquals(stack, _stack)
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

        val instruction = ReferenceInstruction.RefEq

        val refEqExecutor: RefEqExecutor = { _stack ->
            assertEquals(stack, _stack)
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

        val referenceType = referenceType()
        val instruction = ReferenceInstruction.RefTest(referenceType)

        val refTestExecutor: RefTestExecutor = { _store, _stack, _refType ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(referenceType, _refType)
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

        val refCastExecutor: RefCastExecutor = { _store, _stack, _refType ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(referenceType, _refType)
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

        fun refEqExecutor(): RefEqExecutor = { _ ->
            fail("RefEqExecutor should not be called in this scenario")
        }

        fun refTestExecutor(): RefTestExecutor = { _, _, _ ->
            fail("RefTestExecutor should not be called in this scenario")
        }

        fun refCastExecutor(): RefTestExecutor = { _, _, _ ->
            fail("RefCastExecutor should not be called in this scenario")
        }

        fun referenceInstructionExecutor(
            context: ExecutionContext,
            instruction: ReferenceInstruction,
            refNullExecutor: RefNullExecutor = refNullExecutor(),
            refIsNullExecutor: RefIsNullExecutor = refIsNullExecutor(),
            refFuncExecutor: RefFuncExecutor = refFuncExecutor(),
            refAsNonNullExecutor: RefAsNonNullExecutor = refAsNonNullExecutor(),
            refEqExecutor: RefEqExecutor = refEqExecutor(),
            refTestExecutor: RefTestExecutor = refTestExecutor(),
            refCastExecutor: RefCastExecutor = refCastExecutor(),
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
