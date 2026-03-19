@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NumericSuperInstructionPredecoderTest {

    @Test
    fun `predecodes i32 const into frame slots without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32Const(
            value = 7,
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(7L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i32 add without populating load cache`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32Add(
            left = FusedOperand.I32Const(7),
            right = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 5)
        }

        execute(dispatchable, context, vstack)

        assertEquals(12L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i32 eqz from frame slots without populating load cache`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32Eqz(
            operand = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 0L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i32 sub through strict binary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32Sub(
            left = FusedOperand.FrameSlot(0),
            right = FusedOperand.I32Const(3),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 9L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(6L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i32 clz through strict unary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32Clz(
            operand = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 8L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(28L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i32 lt u through strict relop variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32LtU(
            left = FusedOperand.I32Const(1),
            right = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, -1L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i32 trunc f32 s through strict conversion variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32TruncF32S(
            operand = FusedOperand.F32Const(7.75f),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(7L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `preserves i32 div s traps without populating load cache`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I32DivS(
            left = FusedOperand.FrameSlot(0),
            right = FusedOperand.I32Const(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 8)
        }

        val exception = assertFailsWith<InvocationException> {
            execute(dispatchable, context, vstack)
        }

        assertEquals(InvocationError.CannotDivideIntegerByZero, exception.error)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i64 add through strict binary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64Add(
            left = FusedOperand.FrameSlot(0),
            right = FusedOperand.I64Const(3L),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 9L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(12L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i64 eqz through strict unary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64Eqz(
            operand = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 0L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i64 extend i32 u through strict variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64ExtendI32U(
            operand = FusedOperand.I32Const(-1),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(UInt.MAX_VALUE.toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i64 trunc f32 s through strict conversion variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64TruncF32S(
            operand = FusedOperand.F32Const(7.75f),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(7L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `preserves i64 div s traps without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64DivS(
            left = FusedOperand.FrameSlot(0),
            right = FusedOperand.I64Const(0L),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 8L)
        }

        val exception = assertFailsWith<InvocationException> {
            execute(dispatchable, context, vstack)
        }

        assertEquals(InvocationError.CannotDivideIntegerByZero, exception.error)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i64 add128 through strict wide variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64Add128(
            leftLow = FusedOperand.I64Const(1L),
            leftHigh = FusedOperand.FrameSlot(0),
            rightLow = FusedOperand.FrameSlot(3),
            rightHigh = FusedOperand.I64Const(4L),
            destinationLow = FusedDestination.FrameSlot(1),
            destinationHigh = FusedDestination.FrameSlot(2),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(4)
            setFrameSlot(0, 2L)
            setFrameSlot(3, 3L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(4L, vstack.getFrameSlot(1))
        assertEquals(6L, vstack.getFrameSlot(2))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes i64 mul wide u through strict wide variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64MulWideU(
            left = FusedOperand.I64Const(-1L),
            right = FusedOperand.I64Const(2L),
            destinationLow = FusedDestination.FrameSlot(0),
            destinationHigh = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
        }

        execute(dispatchable, context, vstack)

        assertEquals(-2L, vstack.getFrameSlot(0))
        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `rejects i64 add128 with unlowered value stack operands`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.I64Add128(
            leftLow = FusedOperand.I64Const(1L),
            leftHigh = FusedOperand.FrameSlot(0),
            rightLow = FusedOperand.FrameSlot(3),
            rightHigh = FusedOperand.ValueStack,
            destinationLow = FusedDestination.FrameSlot(1),
            destinationHigh = FusedDestination.FrameSlot(2),
        )

        assertFailsWith<IllegalStateException> {
            NumericSuperInstructionPredecoder(context, instruction).value
        }
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f32 add through strict binary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F32Add(
            left = FusedOperand.F32Const(1.5f),
            right = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 2.25f.toRawBits().toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(3.75f.toRawBits().toLong(), vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f32 abs through strict unary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F32Abs(
            operand = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, (-8.5f).toRawBits().toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(8.5f.toRawBits().toLong(), vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f32 ge through strict relop variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F32Ge(
            left = FusedOperand.FrameSlot(0),
            right = FusedOperand.F32Const(1.25f),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 2.5f.toRawBits().toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f32 reinterpret i32 through strict conversion variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F32ReinterpretI32(
            operand = FusedOperand.I32Const(0x3f800000),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(0x3f800000, vstack.getFrameSlot(0).toInt())
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f64 add through strict binary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F64Add(
            left = FusedOperand.F64Const(1.5),
            right = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 2.25.toRawBits())
        }

        execute(dispatchable, context, vstack)

        assertEquals(3.75.toRawBits(), vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f64 abs through strict unary variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F64Abs(
            operand = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, (-8.5).toRawBits())
        }

        execute(dispatchable, context, vstack)

        assertEquals(8.5.toRawBits(), vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f64 le through strict relop variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F64Le(
            left = FusedOperand.FrameSlot(0),
            right = FusedOperand.F64Const(2.5),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 2.5.toRawBits())
        }

        execute(dispatchable, context, vstack)

        assertEquals(1L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f64 convert i64 u through strict conversion variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F64ConvertI64U(
            operand = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, -1L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(ULong.MAX_VALUE.toDouble().toRawBits(), vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f64 promote f32 through strict conversion variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F64PromoteF32(
            operand = FusedOperand.F32Const(7.75f),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(7.75f.toDouble().toRawBits(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes f64 reinterpret i64 through strict conversion variants without populating caches`() {
        val context = predecodingContext()
        val instruction = NumericSuperInstruction.F64ReinterpretI64(
            operand = FusedOperand.I64Const(0x3ff0000000000000L),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = NumericSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(0x3ff0000000000000L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(
        globals: MutableList<GlobalInstance> = mutableListOf(),
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
