package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ControlInstructionExecutorImplTest {

    @Test
    fun ` can execute a nop`() {

        val store = store()
        val stack = Stack()

        val instruction = ControlInstruction.Nop

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a call instruction`() {

        val store = store()
        val stack = Stack()

        val functionIndex = Index.FunctionIndex(0u)
        val instruction = ControlInstruction.Call(functionIndex)

        val callExecutor: CallExecutor = { passedStore, passedStack, passedInstruction ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor,
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a call indirect instruction`() {

        val store = store()
        val stack = Stack()

        val typeIndex = Index.TypeIndex(0u)
        val tableIndex = Index.TableIndex(0u)
        val instruction = ControlInstruction.CallIndirect(typeIndex, tableIndex)

        val callIndirectExecutor: CallIndirectExecutor = { passedStore, passedStack, passedInstruction ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor,
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a block instruction`() {

        val store = store()
        val stack = Stack()

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.Block(blockType, emptyList())

        val blockExecutor: BlockExecutor = { passedStore, passedStack, passedBlockType, passedInstructions ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(blockType, passedBlockType)
            assertEquals(emptyList(), passedInstructions)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor,
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a loop instruction`() {

        val store = store()
        val stack = Stack()

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.Loop(blockType, emptyList())

        val loopExecutor: LoopExecutor = { passedStore, passedStack, passedBlockType, passedInstructions ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(blockType, passedBlockType)
            assertEquals(emptyList(), passedInstructions)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor,
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute an if instruction`() {

        val store = store()
        val stack = Stack()

        val blockType = ControlInstruction.BlockType.Empty
        val instruction = ControlInstruction.If(blockType, emptyList(), null)

        val ifExecutor: IfExecutor = { passedStore, passedStack, passedInstruction ->
            assertEquals(store, passedStore)
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor,
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break instruction`() {

        val store = store()
        val stack = Stack()

        val labelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.Br(labelIndex)

        val breakExecutor: BreakExecutor = { passedStack, passedLabelIndex ->
            assertEquals(stack, passedStack)
            assertEquals(labelIndex, passedLabelIndex)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor,
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break if instruction`() {

        val store = store()
        val stack = Stack()

        val labelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.BrIf(labelIndex)

        val brIfExecutor: BrIfExecutor = { passedStack, passedInstruction ->
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor,
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a break table instruction`() {

        val store = store()
        val stack = Stack()

        val defaultLabelIndex = Index.LabelIndex(0u)
        val instruction = ControlInstruction.BrTable(emptyList(), defaultLabelIndex)

        val brTableExecutor: BrTableExecutor = { passedStack, passedInstruction ->
            assertEquals(stack, passedStack)
            assertEquals(instruction, passedInstruction)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor,
            returnExecutor = returnExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun ` can execute a return instruction`() {

        val store = store()
        val stack = Stack()

        val instruction = ControlInstruction.Return

        val returnExecutor: ReturnExecutor = { passedStack ->
            assertEquals(stack, passedStack)
            Ok(Unit)
        }

        val actual = ControlInstructionExecutorImpl(
            instruction = instruction,
            store = store,
            stack = stack,
            callExecutor = callExecutor(),
            callIndirectExecutor = callIndirectExecutor(),
            blockExecutor = blockExecutor(),
            loopExecutor = loopExecutor(),
            ifExecutor = ifExecutor(),
            breakExecutor = breakExecutor(),
            brIfExecutor = brIfExecutor(),
            brTableExecutor = brTableExecutor(),
            returnExecutor = returnExecutor,
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

        fun returnExecutor(): ReturnExecutor = { _ ->
            fail()
        }
    }
}
