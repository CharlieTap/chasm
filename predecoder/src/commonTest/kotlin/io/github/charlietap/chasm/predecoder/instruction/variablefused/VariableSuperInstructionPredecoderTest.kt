@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.variablefused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.VariableSuperInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VariableSuperInstructionPredecoderTest {

    @Test
    fun `predecodes global get into frame slots without using caches`() {
        val globals = mutableListOf(
            globalInstance(value = 7L),
        )
        val context = predecodingContext(globals)
        val instruction = VariableSuperInstruction.GlobalGet(
            globalIdx = Index.GlobalIndex(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = VariableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
        }

        execute(dispatchable, context, vstack)

        assertEquals(7L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes global set from frame slots without using caches`() {
        val globals = mutableListOf(
            globalInstance(value = 0L),
        )
        val context = predecodingContext(globals)
        val instruction = VariableSuperInstruction.GlobalSet(
            operand = FusedOperand.FrameSlot(0),
            globalIdx = Index.GlobalIndex(0),
        )

        val dispatchable = VariableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
            setFrameSlot(0, 42L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(42L, globals[0].value)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes global set from immediates without using caches`() {
        val globals = mutableListOf(
            globalInstance(value = 0L),
        )
        val context = predecodingContext(globals)
        val instruction = VariableSuperInstruction.GlobalSet(
            operand = FusedOperand.I32Const(9),
            globalIdx = Index.GlobalIndex(0),
        )

        val dispatchable = VariableSuperInstructionPredecoder(context, instruction).value

        execute(dispatchable, context, ValueStack())

        assertEquals(9L, globals[0].value)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes local set from frame slots without using caches`() {
        val context = predecodingContext(mutableListOf())
        val instruction = VariableSuperInstruction.LocalSet(
            operand = FusedOperand.FrameSlot(0),
            localIdx = Index.LocalIndex(1),
        )

        val dispatchable = VariableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 42L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(42L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes local set from immediates without using caches`() {
        val context = predecodingContext(mutableListOf())
        val instruction = VariableSuperInstruction.LocalSet(
            operand = FusedOperand.F64Const(7.0),
            localIdx = Index.LocalIndex(0),
        )

        val dispatchable = VariableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(7.0.toRawBits(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(
        globals: MutableList<GlobalInstance>,
    ): PredecodingContext {
        val instance = moduleInstance(
            globalAddresses = globals.indices
                .map { Address.Global(it) }
                .toMutableList(),
        )
        val store = store(
            globals = globals,
        )

        return PredecodingContext(
            instance = instance,
            store = store,
            instructionCache = hashMapOf(),
            runtimeTypes = mutableListOf(),
        )
    }

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
