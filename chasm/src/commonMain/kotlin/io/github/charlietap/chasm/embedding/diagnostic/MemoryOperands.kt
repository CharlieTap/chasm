package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

/**
 * Slot operands are read from the faulting frame, which the trap left
 * unchanged. [offset] is zero for constant addresses because the compiler
 * folds the static offset into them.
 */
internal class MemoryOperands(
    val address: Long?,
    val offset: Long = 0,
    val length: Long? = null,
    val sourceAddress: Long? = null,
    val segmentLength: Long? = null,
)

internal fun MemoryOperands(
    instruction: LinkedInstruction,
    slots: LongArray,
): MemoryOperands? = when (instruction) {
    is MemoryInstruction.I32LoadI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32LoadS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64LoadI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64LoadS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F32LoadI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F32LoadS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F64LoadI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F64LoadS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load8SI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load8SS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load8UI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load8US -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load16SI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load16SS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load16UI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Load16US -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load8SI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load8SS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load8UI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load8US -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load16SI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load16SS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load16UI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load16US -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load32SI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load32SS -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load32UI -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Load32US -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.MemoryInitIii -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = unsigned(instruction.sourceOffset),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitIis -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = unsigned(instruction.sourceOffset),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitIsi -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitIss -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitSii -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = unsigned(instruction.sourceOffset),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitSis -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = unsigned(instruction.sourceOffset),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitSsi -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryInitSss -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
        segmentLength = instruction.data.bytes.size.toLong(),
    )
    is MemoryInstruction.MemoryCopyIii -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = unsigned(instruction.sourceOffset),
    )
    is MemoryInstruction.MemoryCopyIis -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = unsigned(instruction.sourceOffset),
    )
    is MemoryInstruction.MemoryCopyIsi -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
    )
    is MemoryInstruction.MemoryCopyIss -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = unsigned(instruction.bytesToCopy),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
    )
    is MemoryInstruction.MemoryCopySii -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = unsigned(instruction.sourceOffset),
    )
    is MemoryInstruction.MemoryCopySis -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = unsigned(instruction.sourceOffset),
    )
    is MemoryInstruction.MemoryCopySsi -> MemoryOperands(
        address = unsigned(instruction.destinationOffset),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
    )
    is MemoryInstruction.MemoryCopySss -> MemoryOperands(
        address = slot(slots, instruction.destinationOffsetSlot),
        length = slot(slots, instruction.bytesToCopySlot),
        sourceAddress = slot(slots, instruction.sourceOffsetSlot),
    )
    is MemoryInstruction.MemoryFillIii -> MemoryOperands(
        address = unsigned(instruction.offset),
        length = unsigned(instruction.bytesToFill),
    )
    is MemoryInstruction.MemoryFillIis -> MemoryOperands(
        address = slot(slots, instruction.offsetSlot),
        length = unsigned(instruction.bytesToFill),
    )
    is MemoryInstruction.MemoryFillIsi -> MemoryOperands(
        address = unsigned(instruction.offset),
        length = unsigned(instruction.bytesToFill),
    )
    is MemoryInstruction.MemoryFillIss -> MemoryOperands(
        address = slot(slots, instruction.offsetSlot),
        length = unsigned(instruction.bytesToFill),
    )
    is MemoryInstruction.MemoryFillSii -> MemoryOperands(
        address = unsigned(instruction.offset),
        length = slot(slots, instruction.bytesToFillSlot),
    )
    is MemoryInstruction.MemoryFillSis -> MemoryOperands(
        address = slot(slots, instruction.offsetSlot),
        length = slot(slots, instruction.bytesToFillSlot),
    )
    is MemoryInstruction.MemoryFillSsi -> MemoryOperands(
        address = unsigned(instruction.offset),
        length = slot(slots, instruction.bytesToFillSlot),
    )
    is MemoryInstruction.MemoryFillSss -> MemoryOperands(
        address = slot(slots, instruction.offsetSlot),
        length = slot(slots, instruction.bytesToFillSlot),
    )
    is MemoryInstruction.I32StoreIi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32StoreIs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32StoreSi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32StoreSs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64StoreIi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64StoreIs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64StoreSi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64StoreSs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F32StoreIi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F32StoreIs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F32StoreSi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F32StoreSs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F64StoreIi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F64StoreIs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F64StoreSi -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.F64StoreSs -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store8Ii -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store8Is -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store8Si -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store8Ss -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store16Ii -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store16Is -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store16Si -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I32Store16Ss -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store8Ii -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store8Is -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store8Si -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store8Ss -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store16Ii -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store16Is -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store16Si -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store16Ss -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store32Ii -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store32Is -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store32Si -> MemoryOperands(
        address = unsigned(instruction.address),
        offset = unsigned(instruction.memArg.offset),
    )
    is MemoryInstruction.I64Store32Ss -> MemoryOperands(
        address = slot(slots, instruction.addressSlot),
        offset = unsigned(instruction.memArg.offset),
    )
    else -> null
}

private fun unsigned(value: Int): Long = value.toLong() and UNSIGNED_32_MASK

private fun slot(slots: LongArray, slot: Int): Long? =
    slots.getOrNull(slot)?.let { value -> value and UNSIGNED_32_MASK }

private const val UNSIGNED_32_MASK = 0xFFFF_FFFFL
