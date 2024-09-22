package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.instruction.anyConvertExternInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayFillInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayGetInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayGetSignedInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayGetUnsignedInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayNewDataInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayNewDefaultInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayNewElementInstruction
import io.github.charlietap.chasm.fixture.instruction.arrayNewFixedInstruction
import io.github.charlietap.chasm.fixture.instruction.arraySetInstruction
import io.github.charlietap.chasm.fixture.instruction.externConvertAnyInstruction
import io.github.charlietap.chasm.fixture.instruction.i31GetSignedInstruction
import io.github.charlietap.chasm.fixture.instruction.i31GetUnsignedInstruction
import io.github.charlietap.chasm.fixture.instruction.refI31Instruction
import io.github.charlietap.chasm.fixture.instruction.structGetInstruction
import io.github.charlietap.chasm.fixture.instruction.structGetSignedInstruction
import io.github.charlietap.chasm.fixture.instruction.structGetUnsignedInstruction
import io.github.charlietap.chasm.fixture.instruction.structNewDefaultInstruction
import io.github.charlietap.chasm.fixture.instruction.structNewInstruction
import io.github.charlietap.chasm.fixture.instruction.structSetInstruction
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AggregateInstructionExecutorTest {

    @Test
    fun `delegate the StructNew instruction to the structNewExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val typeIndex = typeIndex()

        val instruction = structNewInstruction(typeIndex)

        val structNewExecutor: Executor<AggregateInstruction.StructNew> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structNewExecutor = structNewExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructNewDefault instruction to the structNewExecutorDefault`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val typeIndex = typeIndex()

        val instruction = structNewDefaultInstruction(typeIndex)

        val structNewDefaultExecutor: Executor<AggregateInstruction.StructNewDefault> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structNewDefaultExecutor = structNewDefaultExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructGet instruction to the structGetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = structGetInstruction(typeIndex(), fieldIndex())

        val structGetExecutor: Executor<AggregateInstruction.StructGet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structGetExecutor = structGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructGetSigned instruction to the structGetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = structGetSignedInstruction(typeIndex(), fieldIndex())

        val structGetExecutor: Executor<AggregateInstruction.StructGetSigned> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structGetSignedExecutor = structGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructGetUnsigned instruction to the structGetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = structGetUnsignedInstruction(typeIndex(), fieldIndex())

        val structGetExecutor: Executor<AggregateInstruction.StructGetUnsigned> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structGetUnsignedExecutor = structGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructSetUnsigned instruction to the structSetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = structSetInstruction(typeIndex(), fieldIndex())

        val structSetExecutor: Executor<AggregateInstruction.StructSet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structSetExecutor = structSetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewFixed instruction to the arrayNewFixedExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val size = 0u
        val instruction = arrayNewFixedInstruction(typeIndex, size)

        val arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayNewFixedExecutor = arrayNewFixedExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewDefault instruction to the arrayNewDefaultExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = arrayNewDefaultInstruction(typeIndex)

        val arrayNewDefaultExecutor: Executor<AggregateInstruction.ArrayNewDefault> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayNewDefaultExecutor = arrayNewDefaultExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewData instruction to the arrayNewDataExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val dataIndex = dataIndex()
        val instruction = arrayNewDataInstruction(typeIndex, dataIndex)

        val arrayNewDataExecutor: Executor<AggregateInstruction.ArrayNewData> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayNewDataExecutor = arrayNewDataExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewElement instruction to the arrayNewElementExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val elementIndex = elementIndex()
        val instruction = arrayNewElementInstruction(typeIndex, elementIndex)

        val arrayNewElementExecutor: Executor<AggregateInstruction.ArrayNewElement> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayNewElementExecutor = arrayNewElementExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayGet instruction to the arrayGetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = arrayGetInstruction(typeIndex)

        val arrayGetExecutor: Executor<AggregateInstruction.ArrayGet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayGetExecutor = arrayGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayGetSigned instruction to the arrayGetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = arrayGetSignedInstruction(typeIndex)

        val arrayGetExecutor: Executor<AggregateInstruction.ArrayGetSigned> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayGetSignedExecutor = arrayGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayGetUnsigned instruction to the arrayGetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = arrayGetUnsignedInstruction(typeIndex)

        val arrayGetExecutor: Executor<AggregateInstruction.ArrayGetUnsigned> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayGetUnsignedExecutor = arrayGetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArraySetUnsigned instruction to the arraySetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = arraySetInstruction(typeIndex)

        val arraySetExecutor: Executor<AggregateInstruction.ArraySet> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arraySetExecutor = arraySetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the RefI31 instruction to the refI31Executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = refI31Instruction()

        val refI31Executor: Executor<AggregateInstruction.RefI31> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            refI31Executor = refI31Executor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the I31GetSigned instruction to the I31GetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = i31GetSignedInstruction()

        val i31GetExecutor: Executor<AggregateInstruction.I31GetSigned> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            i31GetSignedExecutor = i31GetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the I31GetUnsigned instruction to the I31GetExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = i31GetUnsignedInstruction()

        val i31GetExecutor: Executor<AggregateInstruction.I31GetUnsigned> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            i31GetUnsignedExecutor = i31GetExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayFill instruction to the ArrayFillExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = arrayFillInstruction(typeIndex)

        val arrayFillExecutor: Executor<AggregateInstruction.ArrayFill> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            arrayFillExecutor = arrayFillExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the AnyConvertExtern instruction to the AnyConvertExternExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = anyConvertExternInstruction()

        val anyConvertExternExecutor: Executor<AggregateInstruction.AnyConvertExtern> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            anyConvertExternExecutor = anyConvertExternExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ExternConvertAny instruction to the ExternConvertAnyExecutor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = externConvertAnyInstruction()

        val externConvertAnyExecutor: Executor<AggregateInstruction.ExternConvertAny> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = aggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            externConvertAnyExecutor = externConvertAnyExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun structNewExecutor(): Executor<AggregateInstruction.StructNew> = { _, _ ->
            fail("StructNewExecutor should not be called in this scenario")
        }

        fun structNewDefaultExecutor(): Executor<AggregateInstruction.StructNewDefault> = { _, _ ->
            fail("StructNewDefaultExecutor should not be called in this scenario")
        }

        fun structGetExecutor(): Executor<AggregateInstruction.StructGet> = { _, _ ->
            fail("StructGetExecutor should not be called in this scenario")
        }

        fun structGetSignedExecutor(): Executor<AggregateInstruction.StructGetSigned> = { _, _ ->
            fail("StructGetExecutor should not be called in this scenario")
        }

        fun structGetUnsignedExecutor(): Executor<AggregateInstruction.StructGetUnsigned> = { _, _ ->
            fail("StructGetExecutor should not be called in this scenario")
        }

        fun structSetExecutor(): Executor<AggregateInstruction.StructSet> = { _, _ ->
            fail("StructSetExecutor should not be called in this scenario")
        }

        fun arrayNewFixedExecutor(): Executor<AggregateInstruction.ArrayNewFixed> = { _, _ ->
            fail("ArrayNewFixedExecutor should not be called in this scenario")
        }

        fun arrayNewExecutor(): Executor<AggregateInstruction.ArrayNew> = { _, _ ->
            fail("ArrayNewExecutor should not be called in this scenario")
        }

        fun arrayNewDefaultExecutor(): Executor<AggregateInstruction.ArrayNewDefault> = { _, _ ->
            fail("ArrayNewDefaultExecutor should not be called in this scenario")
        }

        fun arrayNewDataExecutor(): Executor<AggregateInstruction.ArrayNewData> = { _, _ ->
            fail("ArrayNewDataExecutor should not be called in this scenario")
        }

        fun arrayNewElementExecutor(): Executor<AggregateInstruction.ArrayNewElement> = { _, _ ->
            fail("ArrayNewElementExecutor should not be called in this scenario")
        }

        fun arrayGetExecutor(): Executor<AggregateInstruction.ArrayGet> = { _, _ ->
            fail("ArrayGetExecutor should not be called in this scenario")
        }

        fun arrayGetSignedExecutor(): Executor<AggregateInstruction.ArrayGetSigned> = { _, _ ->
            fail("ArrayGetExecutor should not be called in this scenario")
        }

        fun arrayGetUnsignedExecutor(): Executor<AggregateInstruction.ArrayGetUnsigned> = { _, _ ->
            fail("ArrayGetExecutor should not be called in this scenario")
        }

        fun arraySetExecutor(): Executor<AggregateInstruction.ArraySet> = { _, _ ->
            fail("ArraySetExecutor should not be called in this scenario")
        }

        fun arrayLenExecutor(): Executor<AggregateInstruction.ArrayLen> = { _, _ ->
            fail("ArrayLenExecutor should not be called in this scenario")
        }

        fun refI31Executor(): Executor<AggregateInstruction.RefI31> = { _, _ ->
            fail("RefI31Executor should not be called in this scenario")
        }

        fun i31GetSignedExecutor(): Executor<AggregateInstruction.I31GetSigned> = { _, _ ->
            fail("I31GetExecutor should not be called in this scenario")
        }

        fun i31GetUnsignedExecutor(): Executor<AggregateInstruction.I31GetUnsigned> = { _, _ ->
            fail("I31GetExecutor should not be called in this scenario")
        }

        fun arrayFillExecutor(): Executor<AggregateInstruction.ArrayFill> = { _, _ ->
            fail("ArrayFillExecutor should not be called in this scenario")
        }

        fun arrayCopyExecutor(): Executor<AggregateInstruction.ArrayCopy> = { _, _ ->
            fail("ArrayCopyExecutor should not be called in this scenario")
        }

        fun arrayInitDataExecutor(): Executor<AggregateInstruction.ArrayInitData> = { _, _ ->
            fail("ArrayInitDataExecutor should not be called in this scenario")
        }

        fun arrayInitElementExecutor(): Executor<AggregateInstruction.ArrayInitElement> = { _, _ ->
            fail("ArrayInitElementExecutor should not be called in this scenario")
        }

        fun anyConvertExternExecutor(): Executor<AggregateInstruction.AnyConvertExtern> = { _, _ ->
            fail("AnyConvertExternExecutor should not be called in this scenario")
        }

        fun externConvertAnyExecutor(): Executor<AggregateInstruction.ExternConvertAny> = { _, _ ->
            fail("ExternConvertAnyExecutor should not be called in this scenario")
        }

        fun aggregateInstructionExecutor(
            context: ExecutionContext,
            instruction: AggregateInstruction,
            structNewExecutor: Executor<AggregateInstruction.StructNew> = structNewExecutor(),
            structNewDefaultExecutor: Executor<AggregateInstruction.StructNewDefault> = structNewDefaultExecutor(),
            structGetExecutor: Executor<AggregateInstruction.StructGet> = structGetExecutor(),
            structGetSignedExecutor: Executor<AggregateInstruction.StructGetSigned> = structGetSignedExecutor(),
            structGetUnsignedExecutor: Executor<AggregateInstruction.StructGetUnsigned> = structGetUnsignedExecutor(),
            structSetExecutor: Executor<AggregateInstruction.StructSet> = structSetExecutor(),
            arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed> = arrayNewFixedExecutor(),
            arrayNewExecutor: Executor<AggregateInstruction.ArrayNew> = arrayNewExecutor(),
            arrayNewDefaultExecutor: Executor<AggregateInstruction.ArrayNewDefault> = arrayNewDefaultExecutor(),
            arrayNewDataExecutor: Executor<AggregateInstruction.ArrayNewData> = arrayNewDataExecutor(),
            arrayNewElementExecutor: Executor<AggregateInstruction.ArrayNewElement> = arrayNewElementExecutor(),
            arrayGetExecutor: Executor<AggregateInstruction.ArrayGet> = arrayGetExecutor(),
            arrayGetSignedExecutor: Executor<AggregateInstruction.ArrayGetSigned> = arrayGetSignedExecutor(),
            arrayGetUnsignedExecutor: Executor<AggregateInstruction.ArrayGetUnsigned> = arrayGetUnsignedExecutor(),
            arraySetExecutor: Executor<AggregateInstruction.ArraySet> = arraySetExecutor(),
            arrayLenExecutor: Executor<AggregateInstruction.ArrayLen> = arrayLenExecutor(),
            refI31Executor: Executor<AggregateInstruction.RefI31> = refI31Executor(),
            i31GetSignedExecutor: Executor<AggregateInstruction.I31GetSigned> = i31GetSignedExecutor(),
            i31GetUnsignedExecutor: Executor<AggregateInstruction.I31GetUnsigned> = i31GetUnsignedExecutor(),
            arrayFillExecutor: Executor<AggregateInstruction.ArrayFill> = arrayFillExecutor(),
            arrayCopyExecutor: Executor<AggregateInstruction.ArrayCopy> = arrayCopyExecutor(),
            arrayInitDataExecutor: Executor<AggregateInstruction.ArrayInitData> = arrayInitDataExecutor(),
            arrayInitElementExecutor: Executor<AggregateInstruction.ArrayInitElement> = arrayInitElementExecutor(),
            anyConvertExternExecutor: Executor<AggregateInstruction.AnyConvertExtern> = anyConvertExternExecutor(),
            externConvertAnyExecutor: Executor<AggregateInstruction.ExternConvertAny> = externConvertAnyExecutor(),
        ) = AggregateInstructionExecutor(
            context = context,
            instruction = instruction,
            structNewExecutor = structNewExecutor,
            structNewDefaultExecutor = structNewDefaultExecutor,
            structGetExecutor = structGetExecutor,
            structGetSignedExecutor = structGetSignedExecutor,
            structGetUnsignedExecutor = structGetUnsignedExecutor,
            structSetExecutor = structSetExecutor,
            arrayNewFixedExecutor = arrayNewFixedExecutor,
            arrayNewExecutor = arrayNewExecutor,
            arrayNewDefaultExecutor = arrayNewDefaultExecutor,
            arrayNewDataExecutor = arrayNewDataExecutor,
            arrayNewElementExecutor = arrayNewElementExecutor,
            arrayGetExecutor = arrayGetExecutor,
            arrayGetSignedExecutor = arrayGetSignedExecutor,
            arrayGetUnsignedExecutor = arrayGetUnsignedExecutor,
            arraySetExecutor = arraySetExecutor,
            arrayLenExecutor = arrayLenExecutor,
            refI31Executor = refI31Executor,
            i31GetSignedExecutor = i31GetSignedExecutor,
            i31GetUnsignedExecutor = i31GetUnsignedExecutor,
            arrayFillExecutor = arrayFillExecutor,
            arrayCopyExecutor = arrayCopyExecutor,
            arrayInitDataExecutor = arrayInitDataExecutor,
            arrayInitElementExecutor = arrayInitElementExecutor,
            anyConvertExternExecutor = anyConvertExternExecutor,
            externConvertAnyExecutor = externConvertAnyExecutor,
        )
    }
}
