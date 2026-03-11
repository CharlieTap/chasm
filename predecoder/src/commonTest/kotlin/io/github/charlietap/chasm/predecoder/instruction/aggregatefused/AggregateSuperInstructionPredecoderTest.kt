@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.structAddress
import io.github.charlietap.chasm.fixture.runtime.instance.structInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.structReferenceValue
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.toI31
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AggregateSuperInstructionPredecoderTest {

    @Test
    fun `predecodes array get into frame slots without using caches`() {
        val runtimeStore = store().apply {
            arrays.add(arrayInstance(fields = longArrayOf(10L, 20L)))
        }
        val context = predecodingContext(store = runtimeStore)
        val instruction = AggregateSuperInstruction.ArrayGet(
            address = FusedOperand.FrameSlot(0),
            field = FusedOperand.I32Const(1),
            destination = FusedDestination.FrameSlot(1),
            typeIndex = Index.TypeIndex(0),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, arrayReferenceValue(arrayAddress(0)).toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(20L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes array copy with mixed operands without using caches`() {
        val runtimeStore = store().apply {
            arrays.add(arrayInstance(fields = longArrayOf(1L, 2L, 3L)))
            arrays.add(arrayInstance(fields = longArrayOf(0L, 0L, 0L)))
        }
        val context = predecodingContext(store = runtimeStore)
        val instruction = AggregateSuperInstruction.ArrayCopy(
            elementsToCopy = FusedOperand.I32Const(2),
            sourceOffset = FusedOperand.FrameSlot(0),
            sourceAddress = FusedOperand.FrameSlot(1),
            destinationOffset = FusedOperand.I32Const(1),
            destinationAddress = FusedOperand.FrameSlot(2),
            sourceTypeIndex = Index.TypeIndex(0),
            destinationTypeIndex = Index.TypeIndex(0),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(0, 0L)
            setFrameSlot(1, arrayReferenceValue(arrayAddress(0)).toLong())
            setFrameSlot(2, arrayReferenceValue(arrayAddress(1)).toLong())
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(0L, 1L, 2L), runtimeStore.arrays[1]!!.fields)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes array new with mixed operands without using caches`() {
        val arrayType = arrayType(fieldType())
        val arrayDefinedType = arrayDefinedType(arrayType)
        val arrayRtt = rtt(type = arrayDefinedType)
        val context = predecodingContext(
            types = listOf(arrayDefinedType),
            runtimeTypes = listOf(arrayRtt),
        )
        val instruction = AggregateSuperInstruction.ArrayNew(
            size = FusedOperand.I32Const(2),
            value = FusedOperand.I32Const(7),
            destination = FusedDestination.FrameSlot(0),
            typeIndex = Index.TypeIndex(0),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(7L, 7L), context.store.arrays[0]!!.fields)
        assertEquals(arrayReferenceValue(arrayAddress(0)).toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes array new default into frame slots without using caches`() {
        val arrayType = arrayType(fieldType())
        val arrayDefinedType = arrayDefinedType(arrayType)
        val arrayRtt = rtt(type = arrayDefinedType)
        val context = predecodingContext(
            types = listOf(arrayDefinedType),
            runtimeTypes = listOf(arrayRtt),
        )
        val instruction = AggregateSuperInstruction.ArrayNewDefault(
            size = FusedOperand.I32Const(2),
            destination = FusedDestination.FrameSlot(0),
            typeIndex = Index.TypeIndex(0),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(0L, 0L), context.store.arrays[0]!!.fields)
        assertEquals(arrayReferenceValue(arrayAddress(0)).toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes array new fixed into frame slots without using caches`() {
        val arrayType = arrayType(fieldType())
        val arrayDefinedType = arrayDefinedType(arrayType)
        val arrayRtt = rtt(type = arrayDefinedType)
        val context = predecodingContext(
            types = listOf(arrayDefinedType),
            runtimeTypes = listOf(arrayRtt),
        )
        val instruction = AggregateSuperInstruction.ArrayNewFixed(
            destination = FusedDestination.FrameSlot(0),
            typeIndex = Index.TypeIndex(0),
            size = 2,
            valueSlots = listOf(1, 2),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(3)
            setFrameSlot(1, 11L)
            setFrameSlot(2, 22L)
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(11L, 22L), context.store.arrays[0]!!.fields)
        assertEquals(arrayReferenceValue(arrayAddress(0)).toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes struct get into frame slots without using caches`() {
        val runtimeStore = store().apply {
            structs.add(structInstance(fields = longArrayOf(10L, 20L)))
        }
        val context = predecodingContext(store = runtimeStore)
        val instruction = AggregateSuperInstruction.StructGet(
            address = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
            typeIndex = Index.TypeIndex(0),
            fieldIndex = Index.FieldIndex(1),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, structReferenceValue(structAddress(0)).toLong())
        }

        execute(dispatchable, context, vstack)

        assertEquals(20L, vstack.getFrameSlot(1))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes struct new default into frame slots without using caches`() {
        val structType = structType(fields = listOf(fieldType(), fieldType()))
        val structDefinedType = structDefinedType(structType)
        val structRtt = rtt(type = structDefinedType)
        val context = predecodingContext(
            types = listOf(structDefinedType),
            runtimeTypes = listOf(structRtt),
        )
        val instruction = AggregateSuperInstruction.StructNewDefault(
            destination = FusedDestination.FrameSlot(0),
            typeIndex = Index.TypeIndex(0),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(0L, 0L), context.store.structs[0]!!.fields)
        assertEquals(structReferenceValue(structAddress(0)).toLong(), vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes ref i31 into frame slots without using caches`() {
        val context = predecodingContext()
        val instruction = AggregateSuperInstruction.RefI31(
            value = FusedOperand.I32Const(17),
            destination = FusedDestination.FrameSlot(0),
        )

        val dispatchable = AggregateSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(17u, vstack.getFrameSlot(0).toI31())
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(
        store: Store = store(),
        types: List<DefinedType> = emptyList(),
        runtimeTypes: List<RTT> = emptyList(),
    ): PredecodingContext = PredecodingContext(
        instance = moduleInstance(runtimeTypes = runtimeTypes.toMutableList()),
        store = store,
        instructionCache = hashMapOf(),
        types = types.toMutableList(),
    )

    private fun arrayDefinedType(
        arrayType: ArrayType,
    ): DefinedType = definedType(
        recursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(
                    compositeType = arrayCompositeType(arrayType),
                ),
            ),
        ),
    )

    private fun structDefinedType(
        structType: StructType,
    ): DefinedType = definedType(
        recursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(
                    compositeType = structCompositeType(structType),
                ),
            ),
        ),
    )

    private fun execute(
        dispatchable: DispatchableInstruction,
        context: PredecodingContext,
        vstack: ValueStack,
    ) {
        val controlStack = cstack()
        val executionContext = executionContext(
            cstack = controlStack,
            vstack = vstack,
            store = context.store,
            instance = context.instance,
        )

        dispatchable(vstack, controlStack, context.store, executionContext)
    }
}
