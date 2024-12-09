package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.module.labelIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.referenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ControlInstructionExecutorTest {

    @Test
    fun ` can execute a nop`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = ControlInstruction.Nop

        val nopExecutor: Executor<ControlInstruction.Nop> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            nopExecutor = nopExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a call instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val functionIndex = Index.FunctionIndex(0u)
        val instruction = ControlInstruction.Call(functionIndex)

        val callExecutor: Executor<ControlInstruction.Call> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            callExecutor = callExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a call indirect instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = Index.TypeIndex(0u)
        val tableIndex = Index.TableIndex(0u)
        val instruction = ControlInstruction.CallIndirect(typeIndex, tableIndex)

        val callIndirectExecutor: Executor<ControlInstruction.CallIndirect> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            callIndirectExecutor = callIndirectExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return call instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val functionIndex = Index.FunctionIndex(0u)
        val instruction = ControlInstruction.ReturnCall(functionIndex)

        val returnCallExecutor: Executor<ControlInstruction.ReturnCall> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            returnCallExecutor = returnCallExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return call indirect instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = Index.TypeIndex(0u)
        val tableIndex = Index.TableIndex(0u)
        val instruction = ControlInstruction.ReturnCallIndirect(typeIndex, tableIndex)

        val returnCallIndirectExecutor: Executor<ControlInstruction.ReturnCallIndirect> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            returnCallIndirectExecutor = returnCallIndirectExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a callref instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = ControlInstruction.CallRef(typeIndex)

        val callRefExecutor: Executor<ControlInstruction.CallRef> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            callRefExecutor = callRefExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a returncallref instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val typeIndex = typeIndex()
        val instruction = ControlInstruction.ReturnCallRef(typeIndex)

        val returnCallRefExecutor: Executor<ControlInstruction.ReturnCallRef> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            returnCallRefExecutor = returnCallRefExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a block instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.Block(blockType, emptyList())

        val blockExecutor: Executor<ControlInstruction.Block> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            blockExecutor = blockExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a loop instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.Loop(blockType, emptyList())

        val loopExecutor: Executor<ControlInstruction.Loop> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            loopExecutor = loopExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute an if instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.If(blockType, emptyList(), null)

        val ifExecutor: Executor<ControlInstruction.If> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            ifExecutor = ifExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val labelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.Br(labelIndex)

        val breakExecutor: Executor<ControlInstruction.Br> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            breakExecutor = breakExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break if instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val labelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.BrIf(labelIndex)

        val brIfExecutor: Executor<ControlInstruction.BrIf> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            brIfExecutor = brIfExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break table instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val defaultLabelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.BrTable(emptyList(), defaultLabelIndex)

        val brTableExecutor: Executor<ControlInstruction.BrTable> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            brTableExecutor = brTableExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on null instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val labelIndex = labelIndex()
        val instruction = ControlInstruction.BrOnNull(labelIndex)

        val brOnNullExecutor: Executor<ControlInstruction.BrOnNull> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            brOnNullExecutor = brOnNullExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on non null instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val labelIndex = labelIndex()
        val instruction = ControlInstruction.BrOnNonNull(labelIndex)

        val brOnNonNullExecutor: Executor<ControlInstruction.BrOnNonNull> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            brOnNonNullExecutor = brOnNonNullExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on cast instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()
        val instruction = ControlInstruction.BrOnCast(labelIndex, srcReferenceType, dstReferenceType)

        val brOnCastExecutor: Executor<ControlInstruction.BrOnCast> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            brOnCastExecutor = brOnCastExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on cast fail instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()
        val instruction = ControlInstruction.BrOnCastFail(labelIndex, srcReferenceType, dstReferenceType)

        val brOnCastFailExecutor: Executor<ControlInstruction.BrOnCastFail> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            brOnCastFailExecutor = brOnCastFailExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = ControlInstruction.Return

        val returnExecutor: Executor<ControlInstruction.Return> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            returnExecutor = returnExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute an unreachable instruction`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val instruction = ControlInstruction.Unreachable

        val unreachableExecutor: Executor<ControlInstruction.Unreachable> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = controlInstructionExecutor(
            context = context,
            instruction = instruction,
            unreachableExecutor = unreachableExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun callExecutor(): Executor<ControlInstruction.Call> = { _, _ ->
            fail()
        }

        fun callIndirectExecutor(): Executor<ControlInstruction.CallIndirect> = { _, _ ->
            fail()
        }

        fun returnCallExecutor(): Executor<ControlInstruction.ReturnCall> = { _, _ ->
            fail()
        }

        fun returnCallIndirectExecutor(): Executor<ControlInstruction.ReturnCallIndirect> = { _, _ ->
            fail()
        }

        fun callRefExecutor(): Executor<ControlInstruction.CallRef> = { _, _ ->
            fail()
        }

        fun returnCallRefExecutor(): Executor<ControlInstruction.ReturnCallRef> = { _, _ ->
            fail()
        }

        fun blockExecutor(): Executor<ControlInstruction.Block> = { _, _ ->
            fail()
        }

        fun loopExecutor(): Executor<ControlInstruction.Loop> = { _, _ ->
            fail()
        }

        fun ifExecutor(): Executor<ControlInstruction.If> = { _, _ ->
            fail()
        }

        fun breakExecutor(): Executor<ControlInstruction.Br> = { _, _ ->
            fail()
        }

        fun brIfExecutor(): Executor<ControlInstruction.BrIf> = { _, _ ->
            fail()
        }

        fun brTableExecutor(): Executor<ControlInstruction.BrTable> = { _, _ ->
            fail()
        }

        fun brOnNullExecutor(): Executor<ControlInstruction.BrOnNull> = { _, _ ->
            fail()
        }

        fun brOnNonNullExecutor(): Executor<ControlInstruction.BrOnNonNull> = { _, _ ->
            fail()
        }

        fun brOnCastExecutor(): Executor<ControlInstruction.BrOnCast> = { _, _ ->
            fail()
        }

        fun brOnCastFailExecutor(): Executor<ControlInstruction.BrOnCastFail> = { _, _ ->
            fail()
        }

        fun nopExecutor(): Executor<ControlInstruction.Nop> = { _, _ ->
            fail()
        }

        fun returnExecutor(): Executor<ControlInstruction.Return> = { _, _ ->
            fail()
        }

        fun throwExecutor(): Executor<ControlInstruction.Throw> = { _, _ ->
            fail()
        }

        fun throwRefExecutor(): Executor<ControlInstruction.ThrowRef> = { _, _ ->
            fail()
        }

        fun tryTableExecutor(): Executor<ControlInstruction.TryTable> = { _, _ ->
            fail()
        }

        fun unreachableExecutor(): Executor<ControlInstruction.Unreachable> = { _, _ ->
            fail()
        }

        fun controlInstructionExecutor(
            context: ExecutionContext,
            instruction: ControlInstruction,
            callExecutor: Executor<ControlInstruction.Call> = callExecutor(),
            callIndirectExecutor: Executor<ControlInstruction.CallIndirect> = callIndirectExecutor(),
            returnCallExecutor: Executor<ControlInstruction.ReturnCall> = returnCallExecutor(),
            returnCallIndirectExecutor: Executor<ControlInstruction.ReturnCallIndirect> = returnCallIndirectExecutor(),
            callRefExecutor: Executor<ControlInstruction.CallRef> = callRefExecutor(),
            returnCallRefExecutor: Executor<ControlInstruction.ReturnCallRef> = returnCallRefExecutor(),
            blockExecutor: Executor<ControlInstruction.Block> = blockExecutor(),
            loopExecutor: Executor<ControlInstruction.Loop> = loopExecutor(),
            ifExecutor: Executor<ControlInstruction.If> = ifExecutor(),
            breakExecutor: Executor<ControlInstruction.Br> = breakExecutor(),
            brIfExecutor: Executor<ControlInstruction.BrIf> = brIfExecutor(),
            brTableExecutor: Executor<ControlInstruction.BrTable> = brTableExecutor(),
            brOnNullExecutor: Executor<ControlInstruction.BrOnNull> = brOnNullExecutor(),
            brOnNonNullExecutor: Executor<ControlInstruction.BrOnNonNull> = brOnNonNullExecutor(),
            brOnCastExecutor: Executor<ControlInstruction.BrOnCast> = brOnCastExecutor(),
            brOnCastFailExecutor: Executor<ControlInstruction.BrOnCastFail> = brOnCastFailExecutor(),
            nopExecutor: Executor<ControlInstruction.Nop> = nopExecutor(),
            returnExecutor: Executor<ControlInstruction.Return> = returnExecutor(),
            throwExecutor: Executor<ControlInstruction.Throw> = throwExecutor(),
            throwRefExecutor: Executor<ControlInstruction.ThrowRef> = throwRefExecutor(),
            tryTableExecutor: Executor<ControlInstruction.TryTable> = tryTableExecutor(),
            unreachableExecutor: Executor<ControlInstruction.Unreachable> = unreachableExecutor(),
        ) = ControlInstructionExecutor(
            context = context,
            instruction = instruction,
            callExecutor = callExecutor,
            callIndirectExecutor = callIndirectExecutor,
            returnCallExecutor = returnCallExecutor,
            returnCallIndirectExecutor = returnCallIndirectExecutor,
            callRefExecutor = callRefExecutor,
            returnCallRefExecutor = returnCallRefExecutor,
            blockExecutor = blockExecutor,
            loopExecutor = loopExecutor,
            ifExecutor = ifExecutor,
            breakExecutor = breakExecutor,
            brIfExecutor = brIfExecutor,
            brTableExecutor = brTableExecutor,
            brOnNullExecutor = brOnNullExecutor,
            brOnNonNullExecutor = brOnNonNullExecutor,
            brOnCastExecutor = brOnCastExecutor,
            brOnCastFailExecutor = brOnCastFailExecutor,
            nopExecutor = nopExecutor,
            returnExecutor = returnExecutor,
            tryTableExecutor = tryTableExecutor,
            throwExecutor = throwExecutor,
            throwRefExecutor = throwRefExecutor,
            unreachableExecutor = unreachableExecutor,
        )
    }
}
