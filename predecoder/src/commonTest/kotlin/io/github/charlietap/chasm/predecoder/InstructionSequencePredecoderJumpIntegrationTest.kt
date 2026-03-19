@file:OptIn(com.github.michaelbull.result.annotation.UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.ir.instruction.blockInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ir.instruction.frameSlotDestination
import io.github.charlietap.chasm.fixture.ir.instruction.frameSlotOperand
import io.github.charlietap.chasm.fixture.ir.instruction.fusedI32Const
import io.github.charlietap.chasm.fixture.ir.instruction.valueBlockType
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.ext.depth
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionSequencePredecoderJumpIntegrationTest {

    @Test
    fun `predecodes handler offsets into continuations`() {
        val context = PredecodingContext(
            instance = moduleInstance(),
            store = store(),
            instructionCache = hashMapOf(),
            runtimeTypes = mutableListOf(),
        )
        val instructions = listOf(
            AdminInstruction.PushHandler(
                handlers = listOf(catchAllRefHandler(labelIndex = labelIndex(0))),
                offsets = listOf(2),
                payloadDestinationSlots = listOf(listOf(1)),
                endOffset = 1,
            ),
            AdminInstruction.PopHandler,
            AdminInstruction.EndFunction,
        )

        val dispatchables = InstructionSequencePredecoderList(context, instructions).value
        val vstack = vstack()
        val cstack = cstack()
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        // Simulate the remaining sequence after the current push_handler instruction has been popped.
        cstack.push(arrayOf(dispatchables[2], dispatchables[1]))
        dispatchables[0](vstack, cstack, context.store, executionContext)

        val handler = cstack.handlers().single()
        assertEquals(1, handler.continuations.size)
        assertEquals(1, handler.continuations.single().size)
        assertEquals(1, handler.instructionsDepth)
    }

    @Test
    fun `executes mixed block and jump sequence for br_if first shape`() {
        val context = PredecodingContext(
            instance = moduleInstance(),
            store = store(),
            instructionCache = hashMapOf(),
            runtimeTypes = mutableListOf(),
        )
        val instructions = listOf(
            blockInstruction(
                blockType = valueBlockType(i32ValueType()),
                instructions = listOf(
                    fusedI32Const(
                        value = 1,
                        destination = frameSlotDestination(3),
                    ),
                    AdminInstruction.CopySlots(
                        sourceSlots = listOf(3),
                        destinationSlots = listOf(2),
                    ),
                    AdminInstruction.EndBlock,
                ),
            ),
            fusedI32Const(
                value = 2,
                destination = frameSlotDestination(4),
            ),
            AdminInstruction.JumpIf(
                operand = frameSlotOperand(4),
                offset = 4,
                takenInstructions = listOf(
                    AdminInstruction.CopySlots(
                        sourceSlots = listOf(2),
                        destinationSlots = listOf(1),
                    ),
                ),
            ),
            AdminInstruction.CopySlots(
                sourceSlots = listOf(2),
                destinationSlots = listOf(1),
            ),
            AdminInstruction.CopySlots(
                sourceSlots = listOf(1),
                destinationSlots = listOf(0),
            ),
            AdminInstruction.EndFunction,
        )

        val dispatchables = InstructionSequencePredecoder(context, instructions).value
        val vstack = vstack().apply {
            reserveFrame(5)
        }
        val cstack = cstack()
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )
        var loop = true
        val exitLoop: (ValueStack, ControlStack, Store, io.github.charlietap.chasm.runtime.execution.ExecutionContext) -> Unit =
            { _, _, _, _ -> loop = false }

        cstack.push(
            frame(
                arity = 1,
                depths = stackDepths(instructions = 1, values = 0),
                instance = context.instance,
                frameSlotMode = true,
            ),
        )
        cstack.push(exitLoop)
        cstack.push(dispatchables)

        while (loop) {
            cstack.popInstruction()(vstack, cstack, context.store, executionContext)
        }

        assertEquals(1, executionContext.depth())
        assertEquals(Ok(listOf(1L)), Ok(listOf(vstack.pop())))
    }

    @Test
    fun `executes mixed block and immediate jump sequence for br_if first shape`() {
        val context = PredecodingContext(
            instance = moduleInstance(),
            store = store(),
            instructionCache = hashMapOf(),
            runtimeTypes = mutableListOf(),
        )
        val instructions = listOf(
            blockInstruction(
                blockType = valueBlockType(i32ValueType()),
                instructions = listOf(
                    fusedI32Const(
                        value = 1,
                        destination = frameSlotDestination(3),
                    ),
                    AdminInstruction.CopySlots(
                        sourceSlots = listOf(3),
                        destinationSlots = listOf(2),
                    ),
                    AdminInstruction.EndBlock,
                ),
            ),
            AdminInstruction.JumpIf(
                operand = io.github.charlietap.chasm.ir.instruction.FusedOperand.I32Const(2),
                offset = 3,
                takenInstructions = listOf(
                    AdminInstruction.CopySlots(
                        sourceSlots = listOf(2),
                        destinationSlots = listOf(1),
                    ),
                ),
            ),
            AdminInstruction.CopySlots(
                sourceSlots = listOf(2),
                destinationSlots = listOf(1),
            ),
            AdminInstruction.CopySlots(
                sourceSlots = listOf(1),
                destinationSlots = listOf(0),
            ),
            AdminInstruction.EndFunction,
        )

        val dispatchables = InstructionSequencePredecoder(context, instructions).value
        val vstack = vstack().apply {
            reserveFrame(4)
        }
        val cstack = cstack()
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )
        var loop = true
        val exitLoop: (ValueStack, ControlStack, Store, io.github.charlietap.chasm.runtime.execution.ExecutionContext) -> Unit =
            { _, _, _, _ -> loop = false }

        cstack.push(
            frame(
                arity = 1,
                depths = stackDepths(instructions = 1, values = 0),
                instance = context.instance,
                frameSlotMode = true,
            ),
        )
        cstack.push(exitLoop)
        cstack.push(dispatchables)

        while (loop) {
            cstack.popInstruction()(vstack, cstack, context.store, executionContext)
        }

        assertEquals(1, executionContext.depth())
        assertEquals(Ok(listOf(1L)), Ok(listOf(vstack.pop())))
    }
}
