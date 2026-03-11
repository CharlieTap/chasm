@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.referencefused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReferenceSuperInstructionPredecoderTest {

    @Test
    fun `predecodes ref eq from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ReferenceSuperInstruction.RefEq(
            reference1 = FusedOperand.FrameSlot(0),
            reference2 = FusedOperand.FrameSlot(1),
            destination = FusedDestination.FrameSlot(2),
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(0, functionReferenceValue().toLong())
            setFrameSlot(1, functionReferenceValue().toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(2))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref is null from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ReferenceSuperInstruction.RefIsNull(
            value = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, nullReferenceValue().toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref as non null from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ReferenceSuperInstruction.RefAsNonNull(
            value = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val reference = functionReferenceValue().toLong()
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, reference)
        }

        execute(dispatchable, context, vstack)

        assertEquals(reference, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref null into frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ReferenceSuperInstruction.RefNull(
            destination = FusedDestination.FrameSlot(0),
            type = refNullReferenceType().heapType,
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(nullReferenceValue().toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref func into frame slots without using caches`() {
        val context = PredecodingContext(
            instance = moduleInstance(
                functionAddresses = mutableListOf(functionAddress(0)),
            ),
            store = store(),
            instructionCache = hashMapOf(),
            types = mutableListOf(),
        )
        val instruction = ReferenceSuperInstruction.RefFunc(
            destination = FusedDestination.FrameSlot(0),
            funcIdx = io.github.charlietap.chasm.ir.module.Index.FunctionIndex(0),
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(functionReferenceValue(functionAddress(0)).toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref test from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ReferenceSuperInstruction.RefTest(
            reference = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
            referenceType = refNullReferenceType(),
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, nullReferenceValue().toLong())
        }

        execute(dispatchable, context, vstack, cstack = referenceCstack(context))

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref cast from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ReferenceSuperInstruction.RefCast(
            reference = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
            referenceType = refNullReferenceType(),
        )

        val dispatchable = ReferenceSuperInstructionPredecoder(context, instruction).value
        val reference = nullReferenceValue().toLong()
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, reference)
        }

        execute(dispatchable, context, vstack, cstack = referenceCstack(context))

        assertEquals(reference, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(): PredecodingContext = PredecodingContext(
        instance = moduleInstance(),
        store = store(),
        instructionCache = hashMapOf(),
        types = mutableListOf(),
    )

    private fun referenceCstack(
        context: PredecodingContext,
    ): ControlStack = cstack(frames = listOf(frame(instance = context.instance)))

    private fun execute(
        dispatchable: DispatchableInstruction,
        context: PredecodingContext,
        vstack: ValueStack,
        cstack: ControlStack = cstack(),
    ) {
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        dispatchable(vstack, cstack, context.store, executionContext)
    }
}
