@file:OptIn(UnsafeResultValueAccess::class)

package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.annotation.UnsafeResultValueAccess
import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.TableSuperInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.loadCache
import io.github.charlietap.chasm.predecoder.storeCache
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableSuperInstructionPredecoderTest {

    @Test
    fun `predecodes table get into frame slots without using caches`() {
        val tables = mutableListOf(
            tableInstance(elements = longArrayOf(10L, 20L)),
        )
        val context = predecodingContext(tables = tables)
        val instruction = TableSuperInstruction.TableGet(
            elementIndex = FusedOperand.I32Const(1),
            destination = FusedDestination.FrameSlot(0),
            tableIdx = Index.TableIndex(0),
        )

        val dispatchable = TableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(20L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes table set from frame slots without using caches`() {
        val tables = mutableListOf(
            tableInstance(elements = longArrayOf(10L, 20L, 30L)),
        )
        val context = predecodingContext(tables = tables)
        val instruction = TableSuperInstruction.TableSet(
            value = FusedOperand.FrameSlot(0),
            elementIdx = FusedOperand.FrameSlot(1),
            tableIdx = Index.TableIndex(0),
        )

        val dispatchable = TableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 99L)
            setFrameSlot(1, 2L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(99L, tables[0].elements[2])
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes table grow with mixed operands without using caches`() {
        val tables = mutableListOf(
            tableInstance(
                type = tableType(limits = limits(max = 5u)),
                elements = longArrayOf(1L, 2L),
            ),
        )
        val context = predecodingContext(tables = tables)
        val instruction = TableSuperInstruction.TableGrow(
            elementsToAdd = FusedOperand.I32Const(2),
            referenceValue = FusedOperand.FrameSlot(0),
            destination = FusedDestination.FrameSlot(1),
            tableIdx = Index.TableIndex(0),
        )

        val dispatchable = TableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 7L)
        }

        execute(dispatchable, context, vstack)

        assertEquals(2L, vstack.getFrameSlot(1))
        assertContentEquals(longArrayOf(1L, 2L, 7L, 7L), tables[0].elements)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes table init with mixed operands without using caches`() {
        val tables = mutableListOf(
            tableInstance(elements = longArrayOf(0L, 0L, 0L)),
        )
        val elements = mutableListOf(
            elementInstance(elements = longArrayOf(8L, 9L, 10L)),
        )
        val context = predecodingContext(tables = tables, elements = elements)
        val instruction = TableSuperInstruction.TableInit(
            elementsToInitialise = FusedOperand.I32Const(2),
            segmentOffset = FusedOperand.FrameSlot(0),
            tableOffset = FusedOperand.I32Const(1),
            elemIdx = Index.ElementIndex(0),
            tableIdx = Index.TableIndex(0),
        )

        val dispatchable = TableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
            setFrameSlot(0, 1L)
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(0L, 9L, 10L), tables[0].elements)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes table copy with mixed operands without using caches`() {
        val tables = mutableListOf(
            tableInstance(elements = longArrayOf(1L, 2L, 3L)),
            tableInstance(elements = longArrayOf(0L, 0L, 0L)),
        )
        val context = predecodingContext(tables = tables)
        val instruction = TableSuperInstruction.TableCopy(
            elementsToCopy = FusedOperand.I32Const(2),
            srcOffset = FusedOperand.FrameSlot(0),
            dstOffset = FusedOperand.FrameSlot(1),
            srcTableIdx = Index.TableIndex(0),
            destTableIdx = Index.TableIndex(1),
        )

        val dispatchable = TableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(2)
            setFrameSlot(0, 0L)
            setFrameSlot(1, 1L)
        }

        execute(dispatchable, context, vstack)

        assertContentEquals(longArrayOf(0L, 1L, 2L), tables[1].elements)
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    @Test
    fun `predecodes table size into frame slots without using caches`() {
        val tables = mutableListOf(
            tableInstance(elements = longArrayOf(5L, 6L, 7L)),
        )
        val context = predecodingContext(tables = tables)
        val instruction = TableSuperInstruction.TableSize(
            destination = FusedDestination.FrameSlot(0),
            tableIdx = Index.TableIndex(0),
        )

        val dispatchable = TableSuperInstructionPredecoder(context, instruction).value
        val vstack = ValueStack().apply {
            reserveFrame(1)
        }

        execute(dispatchable, context, vstack)

        assertEquals(3L, vstack.getFrameSlot(0))
        assertTrue(context.loadCache.isEmpty())
        assertTrue(context.storeCache.isEmpty())
    }

    private fun predecodingContext(
        tables: MutableList<TableInstance>,
        elements: MutableList<ElementInstance> = mutableListOf(),
    ): PredecodingContext {
        val instance = moduleInstance(
            tableAddresses = tables.indices
                .map { Address.Table(it) }
                .toMutableList(),
            elemAddresses = elements.indices
                .map { Address.Element(it) }
                .toMutableList(),
        )
        val store = store(
            tables = tables,
            elements = elements,
        )

        return PredecodingContext(
            instance = instance,
            store = store,
            instructionCache = hashMapOf(),
            types = mutableListOf(),
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
