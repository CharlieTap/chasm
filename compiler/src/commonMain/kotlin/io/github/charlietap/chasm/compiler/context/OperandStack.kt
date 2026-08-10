package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.type.ValueType

internal class OperandStack(
    private val pool: ArrayList<Operand> = ArrayList(),
) : AbstractList<Operand>() {

    private var highestReservedSlots = IntArray(INITIAL_CAPACITY)
    private var reservedSlotCounts = IntArray(INITIAL_CAPACITY)
    private var firstUnmaterializedIndex = NO_INDEX

    override var size: Int = 0
        private set

    override fun get(index: Int): Operand {
        checkElementIndex(index, size)
        return pool[index]
    }

    fun push(
        type: ValueType?,
        reservedSlot: Int,
        sourceKind: OperandSourceKind,
        sourceBits: Long,
        sourceLocalIndex: Int = Operand.NO_LOCAL_INDEX,
    ): Operand {
        if (size == highestReservedSlots.size) {
            highestReservedSlots = highestReservedSlots.copyOf(highestReservedSlots.size * 2)
        }
        if (reservedSlot >= reservedSlotCounts.size) {
            var capacity = reservedSlotCounts.size
            while (reservedSlot >= capacity) capacity *= 2
            reservedSlotCounts = reservedSlotCounts.copyOf(capacity)
        }
        val operand = if (size < pool.size) pool[size] else Operand().also(pool::add)
        operand.stackIndex = size
        operand.reset(type, reservedSlot, sourceKind, sourceBits, sourceLocalIndex)
        highestReservedSlots[size] = if (size == 0) {
            reservedSlot
        } else {
            maxOf(highestReservedSlots[size - 1], reservedSlot)
        }
        reservedSlotCounts[reservedSlot]++
        if (sourceKind != OperandSourceKind.Frame && firstUnmaterializedIndex == NO_INDEX) {
            firstUnmaterializedIndex = size
        }
        size++
        return operand
    }

    fun pop(): Operand {
        val index = size - 1
        val operand = pool[index]
        size = index
        reservedSlotCounts[operand.reservedSlot]--
        if (firstUnmaterializedIndex == index) firstUnmaterializedIndex = NO_INDEX
        return operand
    }

    fun pooled(index: Int): Operand {
        checkElementIndex(index, pool.size)
        return pool[index]
    }

    fun highestReservedSlot(): Int {
        val endIndex = size
        return if (endIndex == 0) NO_RESERVED_SLOT else highestReservedSlots[endIndex - 1]
    }

    fun highestReservedSlot(endIndex: Int): Int {
        check(endIndex in 0..size)
        return if (endIndex == 0) NO_RESERVED_SLOT else highestReservedSlots[endIndex - 1]
    }

    fun containsReservedSlot(slot: Int): Boolean =
        slot in reservedSlotCounts.indices && reservedSlotCounts[slot] != 0

    fun firstUnmaterializedIndex(): Int = firstUnmaterializedIndex

    fun markMaterialized(operand: Operand) {
        val index = operand.stackIndex
        if (index != firstUnmaterializedIndex || index >= size) return

        var nextIndex = index + 1
        while (nextIndex < size && pool[nextIndex].sourceKind == OperandSourceKind.Frame) {
            nextIndex++
        }
        firstUnmaterializedIndex = if (nextIndex < size) nextIndex else NO_INDEX
    }

    private companion object {
        const val INITIAL_CAPACITY = 8
        const val NO_INDEX = -1
        const val NO_RESERVED_SLOT = -1
    }
}

internal class PoppedOperands(
    private val operands: OperandStack,
) : AbstractList<Operand>() {

    private var startIndex = 0

    override var size: Int = 0
        private set

    override fun get(index: Int): Operand {
        checkElementIndex(index, size)
        return operands.pooled(startIndex + index)
    }

    fun reset(startIndex: Int, size: Int): PoppedOperands {
        this.startIndex = startIndex
        this.size = size
        return this
    }
}

private fun checkElementIndex(index: Int, size: Int) {
    if (index !in 0 until size) throw IndexOutOfBoundsException("index: $index, size: $size")
}
