package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.module.labelIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.referenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ControlInstructionExecutorImplTest {

    @Test
    fun ` can execute a nop`() {

        val store = store()
        val stack = stack()

        val instruction = ControlInstruction.Nop

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a call instruction`() {

        val store = store()
        val stack = stack()

        val functionIndex = Index.FunctionIndex(0u)
        val instruction = ControlInstruction.Call(functionIndex)

        val callExecutor: CallExecutor = { _store, _stack, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor,
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a call indirect instruction`() {

        val store = store()
        val stack = stack()

        val typeIndex = Index.TypeIndex(0u)
        val tableIndex = Index.TableIndex(0u)
        val instruction = ControlInstruction.CallIndirect(typeIndex, tableIndex)

        val callIndirectExecutor: CallIndirectExecutor = { _store, _stack, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor,
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return call instruction`() {

        val store = store()
        val stack = stack()

        val functionIndex = Index.FunctionIndex(0u)
        val instruction = ControlInstruction.ReturnCall(functionIndex)

        val returnCallExecutor: ReturnCallExecutor = { _store, _stack, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor,
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return call indirect instruction`() {

        val store = store()
        val stack = stack()

        val typeIndex = Index.TypeIndex(0u)
        val tableIndex = Index.TableIndex(0u)
        val instruction = ControlInstruction.ReturnCallIndirect(typeIndex, tableIndex)

        val returnCallIndirectExecutor: ReturnCallIndirectExecutor = { _store, _stack, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor,
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a callref instruction`() {

        val store = store()
        val stack = stack()

        val typeIndex = typeIndex()
        val instruction = ControlInstruction.CallRef(typeIndex)

        val callRefExecutor: CallRefExecutor = { _store, _stack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor,
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a returncallref instruction`() {

        val store = store()
        val stack = stack()

        val typeIndex = typeIndex()
        val instruction = ControlInstruction.ReturnCallRef(typeIndex)

        val returnCallRefExecutor: ReturnCallRefExecutor = { _store, _stack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor,
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a block instruction`() {

        val store = store()
        val stack = stack()

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.Block(blockType, emptyList())

        val blockExecutor: BlockExecutor = { _store, _stack, _blockType, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(blockType, _blockType)
            assertEquals(emptyList(), _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor,
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a loop instruction`() {

        val store = store()
        val stack = stack()

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.Loop(blockType, emptyList())

        val loopExecutor: LoopExecutor = { _store, _stack, _blockType, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(blockType, _blockType)
            assertEquals(emptyList(), _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor,
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute an if instruction`() {

        val store = store()
        val stack = stack()

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.If(blockType, emptyList(), null)

        val ifExecutor: IfExecutor = { _store, _stack, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor,
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break instruction`() {

        val store = store()
        val stack = stack()

        val labelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.Br(labelIndex)

        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor,
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break if instruction`() {

        val store = store()
        val stack = stack()

        val labelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.BrIf(labelIndex)

        val brIfExecutor: BrIfExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor,
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break table instruction`() {

        val store = store()
        val stack = stack()

        val defaultLabelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.BrTable(emptyList(), defaultLabelIndex)

        val brTableExecutor: BrTableExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor,
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on null instruction`() {

        val store = store()
        val stack = stack()

        val labelIndex = labelIndex()
        val instruction = ControlInstruction.BrOnNull(labelIndex)

        val brOnNullExecutor: BrOnNullExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor,
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on non null instruction`() {

        val store = store()
        val stack = stack()

        val labelIndex = labelIndex()
        val instruction = ControlInstruction.BrOnNonNull(labelIndex)

        val brOnNonNullExecutor: BrOnNonNullExecutor = { _stack, _instruction ->
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor,
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on cast instruction`() {

        val store = store()
        val stack = stack()

        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()
        val instruction = ControlInstruction.BrOnCast(labelIndex, srcReferenceType, dstReferenceType)
        val breakIfMatches = true

        val brOnCastExecutor: BrOnCastExecutor = { _store, _stack, _labelIndex, _srcReferenceType, _dstReferenceType, _breakIfMatches ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)
            assertEquals(srcReferenceType, _srcReferenceType)
            assertEquals(dstReferenceType, _dstReferenceType)
            assertEquals(breakIfMatches, _breakIfMatches)

            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor,
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break on cast fail instruction`() {

        val store = store()
        val stack = stack()

        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()
        val instruction = ControlInstruction.BrOnCastFail(labelIndex, srcReferenceType, dstReferenceType)
        val breakIfMatches = false

        val brOnCastExecutor: BrOnCastExecutor = { _store, _stack, _labelIndex, _srcReferenceType, _dstReferenceType, _breakIfMatches ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)
            assertEquals(srcReferenceType, _srcReferenceType)
            assertEquals(dstReferenceType, _dstReferenceType)
            assertEquals(breakIfMatches, _breakIfMatches)

            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor,
            returnExecutor = returnExecutor(),
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return instruction`() {

        val store = store()
        val stack = stack()

        val instruction = ControlInstruction.Return

        val returnExecutor: ReturnExecutor = { _stack ->
            assertEquals(stack, _stack)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            returnCallExecutor = returnCallExecutor(),
            returnCallIndirectExecutor = returnCallIndirectExecutor(),
            callRefExecutor = callRefExecutor(),
            returnCallRefExecutor = returnCallRefExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            brOnNullExecutor = brOnNullExecutor(),
            brOnNonNullExecutor = brOnNonNullExecutor(),
            brOnCastExecutor = brOnCastExecutor(),
            returnExecutor = returnExecutor,
            tryTableExecutor = tryTableExecutor(),
            throwExecutor = throwExecutor(),
            throwRefExecutor = throwRefExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun callExecutor(): CallExecutor = { _, _, _ ->
            fail()
        }

        fun callIndirectExecutor(): CallIndirectExecutor = { _, _, _ ->
            fail()
        }

        fun returnCallExecutor(): ReturnCallExecutor = { _, _, _ ->
            fail()
        }

        fun returnCallIndirectExecutor(): ReturnCallIndirectExecutor = { _, _, _ ->
            fail()
        }

        fun callRefExecutor(): CallRefExecutor = { _, _ ->
            fail()
        }

        fun returnCallRefExecutor(): ReturnCallRefExecutor = { _, _ ->
            fail()
        }

        fun blockExecutor(): BlockExecutor = { _, _, _, _ ->
            fail()
        }

        fun loopExecutor(): LoopExecutor = { _, _, _, _ ->
            fail()
        }

        fun ifExecutor(): IfExecutor = { _, _, _ ->
            fail()
        }

        fun breakExecutor(): BreakExecutor = { _, _ ->
            fail()
        }

        fun brIfExecutor(): BrIfExecutor = { _, _ ->
            fail()
        }

        fun brTableExecutor(): BrTableExecutor = { _, _ ->
            fail()
        }

        fun brOnNullExecutor(): BrOnNullExecutor = { _, _ ->
            fail()
        }

        fun brOnNonNullExecutor(): BrOnNonNullExecutor = { _, _ ->
            fail()
        }

        fun brOnCastExecutor(): BrOnCastExecutor = { _, _, _, _, _, _ ->
            fail()
        }

        fun returnExecutor(): ReturnExecutor = { _ ->
            fail()
        }

        fun throwExecutor(): ThrowExecutor = { _, _, _ ->
            fail()
        }

        fun throwRefExecutor(): ThrowRefExecutor = { _, _, _ ->
            fail()
        }

        fun tryTableExecutor(): TryTableExecutor = { _, _, _ ->
            fail()
        }
    }
}
