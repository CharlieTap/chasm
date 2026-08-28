package io.github.charlietap.chasm.runtime.instruction

import kotlin.jvm.JvmInline

/**
 * Compile-selected movement of call or branch inputs into consecutive
 * destination slots. The containing instruction supplies the destination base.
 */
class OperandTransfer(
    val sources: Array<TransferSource>,
    destinationSlotBase: Int,
) {

    internal val schedule = compileOperandTransferSchedule(sources, destinationSlotBase)

    val isInPlace: Boolean
        get() = schedule.operationCount == 0

    val operationCount: Int
        get() = schedule.operationCount

    val scratchSaveCount: Int
        get() = schedule.scratchSaveCount

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is OperandTransfer && sources.contentEquals(other.sources) && schedule == other.schedule

    override fun hashCode(): Int = 31 * sources.contentHashCode() + schedule.hashCode()

    override fun toString(): String = "OperandTransfer(sources=${sources.contentToString()}, schedule=$schedule)"
}

data class TailCallOperandTransfer(
    val wasm: OperandTransfer,
    val host: OperandTransfer,
)

sealed interface TransferSource {

    @JvmInline
    value class Immediate(val value: Long) : TransferSource

    @JvmInline
    value class Slot(val slot: Int) : TransferSource
}

internal class OperandTransferSchedule(
    val operations: IntArray,
    val values: LongArray,
    val scratchSaveCount: Int,
) {
    val operationCount: Int
        get() = operations.size

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is OperandTransferSchedule &&
            operations.contentEquals(other.operations) &&
            values.contentEquals(other.values)

    override fun hashCode(): Int = 31 * operations.contentHashCode() + values.contentHashCode()

    override fun toString(): String =
        "OperandTransferSchedule(operationCount=$operationCount, scratchSaveCount=$scratchSaveCount)"
}

internal const val OPERAND_TRANSFER_COPY_SLOT = 0
internal const val OPERAND_TRANSFER_COPY_IMMEDIATE = 1
internal const val OPERAND_TRANSFER_SAVE_SLOT = 2
internal const val OPERAND_TRANSFER_RESTORE = 3
internal const val OPERAND_TRANSFER_OPERATION_SHIFT = 30
internal const val OPERAND_TRANSFER_INDEX_MASK = (1 shl OPERAND_TRANSFER_OPERATION_SHIFT) - 1

private fun compileOperandTransferSchedule(
    sources: Array<TransferSource>,
    destinationSlotBase: Int,
): OperandTransferSchedule {
    val remaining = BooleanArray(sources.size)
    var remainingCount = 0
    for (index in sources.indices) {
        val source = sources[index]
        if (source !is TransferSource.Slot || source.slot != destinationSlotBase + index) {
            remaining[index] = true
            remainingCount++
        }
    }
    if (remainingCount == 0) {
        return OperandTransferSchedule(IntArray(0), LongArray(0), scratchSaveCount = 0)
    }

    val operations = IntArray(sources.size * 2)
    val values = LongArray(sources.size * 2)
    var operationCount = 0
    var scratchSaveCount = 0
    var pendingRestore = -1

    fun emit(
        operation: Int,
        index: Int,
        value: Long,
    ) {
        operations[operationCount] = (operation shl OPERAND_TRANSFER_OPERATION_SHIFT) or index
        values[operationCount] = value
        operationCount++
    }

    while (remainingCount > 0) {
        var safeMove = -1
        for (candidate in sources.indices) {
            if (!remaining[candidate]) continue
            val destinationSlot = destinationSlotBase + candidate
            var isNeeded = false
            for (other in sources.indices) {
                if (!remaining[other]) continue
                val source = sources[other]
                if (source is TransferSource.Slot && source.slot == destinationSlot) {
                    isNeeded = true
                    break
                }
            }
            if (!isNeeded) {
                safeMove = candidate
                break
            }
        }

        if (safeMove >= 0) {
            when (val source = sources[safeMove]) {
                is TransferSource.Immediate -> emit(OPERAND_TRANSFER_COPY_IMMEDIATE, safeMove, source.value)
                is TransferSource.Slot -> emit(OPERAND_TRANSFER_COPY_SLOT, safeMove, source.slot.toLong())
            }
            remaining[safeMove] = false
            remainingCount--
            continue
        }

        if (pendingRestore >= 0) {
            emit(OPERAND_TRANSFER_RESTORE, pendingRestore, 0L)
            pendingRestore = -1
            continue
        }

        val cycleMove = remaining.indexOfFirst { it }
        val source = sources[cycleMove] as TransferSource.Slot
        emit(OPERAND_TRANSFER_SAVE_SLOT, 0, source.slot.toLong())
        scratchSaveCount++
        pendingRestore = cycleMove
        remaining[cycleMove] = false
        remainingCount--
    }

    if (pendingRestore >= 0) {
        emit(OPERAND_TRANSFER_RESTORE, pendingRestore, 0L)
    }

    return OperandTransferSchedule(
        operations = operations.copyOf(operationCount),
        values = values.copyOf(operationCount),
        scratchSaveCount = scratchSaveCount,
    )
}
