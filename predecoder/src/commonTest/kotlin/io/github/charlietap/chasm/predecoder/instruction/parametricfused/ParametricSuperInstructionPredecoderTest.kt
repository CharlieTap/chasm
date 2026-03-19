@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.parametricfused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.ParametricSuperInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParametricSuperInstructionPredecoderTest {

    @Test
    fun `predecodes select with mixed immediates and frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ParametricSuperInstruction.Select(
            const = FusedOperand.I32Const(0),
            val1 = FusedOperand.I32Const(11),
            val2 = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = ParametricSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 22L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(22L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes select from frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = ParametricSuperInstruction.Select(
            const = FusedOperand.FrameSlot(0),
            val1 = FusedOperand.FrameSlot(1),
            val2 = FusedOperand.FrameSlot(2),
            destination = FusedDestination.FrameSlot(3),
        )

        val dispatchable = ParametricSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(4)
            setFrameSlot(0, 1L)
            setFrameSlot(1, 11L)
            setFrameSlot(2, 22L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(11L, vstack.getFrameSlot(3))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(): PredecodingContext = PredecodingContext(
        instance = moduleInstance(),
        store = store(),
        instructionCache = hashMapOf(),
        runtimeTypes = mutableListOf(),
    )

    private fun execute(
        dispatchable: DispatchableInstruction,
        context: PredecodingContext,
        vstack: ValueStack,
    ) {
        val cstack = cstack()
        val executionContext = executionContext(
            cstack = cstack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        dispatchable(vstack, cstack, context.store, executionContext)
    }
}
