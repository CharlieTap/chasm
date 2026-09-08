package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind

/**
 * Compiler-side description of simultaneous transfers between frame slots.
 *
 * The single-value representation keeps the common case scalar. Larger
 * transfers retain their source and destination vectors for overlap analysis
 * and lowering to [io.github.charlietap.chasm.runtime.instruction.OperandTransfer].
 */
internal class SlotTransfer private constructor(
    val size: Int,
    private val firstSourceKind: OperandSourceKind,
    private val firstSourceBits: Long,
    private val firstSourceSlot: Int,
    private val firstDestinationSlot: Int,
    private val sourceKinds: ByteArray?,
    private val sourceBits: LongArray?,
    private val sourceSlots: IntArray?,
    private val destinationSlots: IntArray?,
) {

    fun sourceKind(index: Int): OperandSourceKind = if (sourceKinds == null) {
        firstSourceKind
    } else {
        OperandSourceKind.entries[sourceKinds[index].toInt()]
    }

    fun sourceBits(index: Int): Long = sourceBits?.get(index) ?: firstSourceBits

    fun sourceSlot(index: Int): Int = sourceSlots?.get(index) ?: firstSourceSlot

    fun destinationSlot(index: Int): Int = destinationSlots?.get(index) ?: firstDestinationSlot

    fun isIdentity(): Boolean {
        for (index in 0 until size) {
            val sourceSlot = when (sourceKind(index)) {
                OperandSourceKind.Local -> sourceBits(index).toInt()
                OperandSourceKind.Frame -> sourceSlot(index)
                else -> return false
            }
            if (sourceSlot != destinationSlot(index)) return false
        }
        return true
    }

    companion object {
        val Empty = SlotTransfer(
            size = 0,
            firstSourceKind = OperandSourceKind.Frame,
            firstSourceBits = 0,
            firstSourceSlot = 0,
            firstDestinationSlot = 0,
            sourceKinds = null,
            sourceBits = null,
            sourceSlots = null,
            destinationSlots = null,
        )

        fun create(
            operands: List<Operand>,
            operandStartIndex: Int,
            destinationSlots: IntArray,
        ): SlotTransfer {
            check(operandStartIndex >= 0 && operandStartIndex + destinationSlots.size <= operands.size)
            if (destinationSlots.isEmpty()) return Empty
            val first = operands[operandStartIndex]
            if (destinationSlots.size == 1) {
                return SlotTransfer(
                    size = 1,
                    firstSourceKind = first.sourceKind,
                    firstSourceBits = first.sourceBits,
                    firstSourceSlot = first.reservedSlot,
                    firstDestinationSlot = destinationSlots[0],
                    sourceKinds = null,
                    sourceBits = null,
                    sourceSlots = null,
                    destinationSlots = null,
                )
            }

            var allFrameSources = true
            val sourceSlots = IntArray(destinationSlots.size)
            for (index in destinationSlots.indices) {
                val operand = operands[operandStartIndex + index]
                if (operand.sourceKind != OperandSourceKind.Frame) allFrameSources = false
                sourceSlots[index] = operand.reservedSlot
            }
            val sourceKinds = if (allFrameSources) null else ByteArray(destinationSlots.size)
            val sourceBits = if (allFrameSources) null else LongArray(destinationSlots.size)
            if (!allFrameSources) {
                val kinds = checkNotNull(sourceKinds)
                val bits = checkNotNull(sourceBits)
                for (index in destinationSlots.indices) {
                    val operand = operands[operandStartIndex + index]
                    kinds[index] = operand.sourceKind.ordinal.toByte()
                    bits[index] = operand.sourceBits
                }
            }
            return SlotTransfer(
                size = destinationSlots.size,
                firstSourceKind = first.sourceKind,
                firstSourceBits = first.sourceBits,
                firstSourceSlot = first.reservedSlot,
                firstDestinationSlot = destinationSlots[0],
                sourceKinds = sourceKinds,
                sourceBits = sourceBits,
                sourceSlots = sourceSlots,
                destinationSlots = destinationSlots,
            )
        }
    }
}

internal val emptySlotTransfer = SlotTransfer.Empty
