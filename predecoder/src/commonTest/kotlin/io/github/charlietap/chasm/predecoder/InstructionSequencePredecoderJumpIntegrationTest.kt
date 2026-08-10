@file:OptIn(com.github.michaelbull.result.annotation.UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder

import io.github.charlietap.chasm.fixture.ir.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.NumericCondition
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionSequencePredecoderJumpIntegrationTest {

    @Test
    fun `predecodes handler offsets into program addresses`() {
        val context = context()
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
        val baseIp = 100
        val dispatchables = InstructionSequencePredecoder(context, instructions, baseIp).value
        val vstack = vstack()
        val cstack = cstack(frames = listOf(frame(instance = context.instance)))

        dispatchables[0](
            vstack,
            cstack,
            context.store,
            executionContext(cstack, vstack, context.store, context.instance),
            baseIp + 1,
        )

        assertEquals(baseIp + 2, cstack.handlers().single().continuationIps.single())
    }

    @Test
    fun `predecodes taken and fallthrough jump addresses`() {
        val context = context()
        val baseIp = 100
        val taken = InstructionSequencePredecoder(
            context,
            listOf(AdminInstruction.JumpIf(FusedOperand.I32Const(1), offset = 2)),
            baseIp,
        ).value.single()
        val fallthrough = InstructionSequencePredecoder(
            context,
            listOf(AdminInstruction.JumpIf(FusedOperand.I32Const(0), offset = 2)),
            baseIp,
        ).value.single()
        val vstack = vstack()
        val cstack = cstack()
        val executionContext = executionContext(cstack, vstack, context.store, context.instance)

        assertEquals(baseIp + 2, taken(vstack, cstack, context.store, executionContext, baseIp + 1))
        assertEquals(baseIp + 1, fallthrough(vstack, cstack, context.store, executionContext, baseIp + 1))
    }

    @Test
    fun `copies a single branch result only on the taken path`() {
        val context = context()
        val baseIp = 100
        val branch = InstructionSequencePredecoder(
            context,
            listOf(
                AdminInstruction.JumpIfCopy(
                    operand = FusedOperand.FrameSlot(0),
                    sourceSlot = 1,
                    destinationSlot = 2,
                    offset = 2,
                ),
            ),
            baseIp,
        ).value.single()
        val cstack = cstack()
        val takenStack = vstack().apply {
            setFrameSlot(0, 1)
            setFrameSlot(1, 37)
            setFrameSlot(2, 0)
        }
        val fallthroughStack = vstack().apply {
            setFrameSlot(0, 0)
            setFrameSlot(1, 41)
            setFrameSlot(2, 0)
        }

        assertEquals(
            baseIp + 2,
            branch(
                takenStack,
                cstack,
                context.store,
                executionContext(cstack, takenStack, context.store, context.instance),
                baseIp + 1,
            ),
        )
        assertEquals(37, takenStack.getFrameSlot(2))
        assertEquals(
            baseIp + 1,
            branch(
                fallthroughStack,
                cstack,
                context.store,
                executionContext(cstack, fallthroughStack, context.store, context.instance),
                baseIp + 1,
            ),
        )
        assertEquals(0, fallthroughStack.getFrameSlot(2))
    }

    @Test
    fun `predecodes numeric conditions into direct jumps`() {
        val context = context()
        val baseIp = 100
        val conditions = listOf(
            NumericCondition.I32Eqz(FusedOperand.I32Const(0)) to true,
            NumericCondition.I32LtU(FusedOperand.I32Const(-1), FusedOperand.I32Const(0)) to false,
            NumericCondition.I64GtU(FusedOperand.I64Const(-1), FusedOperand.I64Const(0)) to true,
            NumericCondition.F32Ne(FusedOperand.F32Const(Float.NaN), FusedOperand.F32Const(Float.NaN)) to true,
            NumericCondition.F64Eq(FusedOperand.F64Const(-0.0), FusedOperand.F64Const(0.0)) to true,
            NumericCondition.I32LtS(FusedOperand.FrameSlot(0), FusedOperand.I32Const(10)) to true,
        )
        val vstack = vstack().apply { setFrameSlot(0, 5) }
        val cstack = cstack()
        val executionContext = executionContext(cstack, vstack, context.store, context.instance)

        conditions.forEach { (condition, taken) ->
            val branch = InstructionSequencePredecoder(
                context,
                listOf(AdminInstruction.JumpIfCondition(condition, offset = 2)),
                baseIp,
            ).value.single()

            assertEquals(
                if (taken) baseIp + 2 else baseIp + 1,
                branch(vstack, cstack, context.store, executionContext, baseIp + 1),
                condition.toString(),
            )
        }
    }

    @Test
    fun `predecodes unfused stack branches without a label stack`() {
        val context = context()
        val baseIp = 100
        val branch = InstructionSequencePredecoder(
            context,
            listOf(AdminInstruction.JumpIf(FusedOperand.ValueStack, offset = 2)),
            baseIp,
        ).value.single()
        val vstack = vstack().apply { pushI32(1) }
        val cstack = cstack()

        val result = branch(
            vstack,
            cstack,
            context.store,
            executionContext(cstack, vstack, context.store, context.instance),
            baseIp + 1,
        )

        assertEquals(baseIp + 2, result)
        assertEquals(0, vstack.depth())
    }

    private fun context() = PredecodingContext(
        instance = moduleInstance(),
        store = store(),
        instructionCache = hashMapOf(),
        runtimeTypes = mutableListOf(),
    )
}
