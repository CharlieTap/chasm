package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction

sealed interface BulkMemory {

    class Copy(
        val source: MemoryIndex,
        val destination: MemoryIndex,
        val count: Operand,
        val sourceOffset: Operand,
        val destinationOffset: Operand,
    ) : BulkMemory

    class Fill(
        val memory: MemoryIndex,
        val count: Operand,
        val value: Operand,
        val offset: Operand,
    ) : BulkMemory
}

fun MemoryInstruction.bulk(): BulkMemory? = when (val i = this) {
    is MemoryInstruction.MemoryCopyIii ->
        copy(i.srcMemory, i.dstMemory, immediate(i.bytesToCopy), immediate(i.sourceOffset), immediate(i.destinationOffset))
    is MemoryInstruction.MemoryCopyIis ->
        copy(i.srcMemory, i.dstMemory, immediate(i.bytesToCopy), immediate(i.sourceOffset), slot(i.destinationOffsetSlot))
    is MemoryInstruction.MemoryCopyIsi ->
        copy(i.srcMemory, i.dstMemory, immediate(i.bytesToCopy), slot(i.sourceOffsetSlot), immediate(i.destinationOffset))
    is MemoryInstruction.MemoryCopyIss ->
        copy(i.srcMemory, i.dstMemory, immediate(i.bytesToCopy), slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot))
    is MemoryInstruction.MemoryCopySii ->
        copy(i.srcMemory, i.dstMemory, slot(i.bytesToCopySlot), immediate(i.sourceOffset), immediate(i.destinationOffset))
    is MemoryInstruction.MemoryCopySis ->
        copy(i.srcMemory, i.dstMemory, slot(i.bytesToCopySlot), immediate(i.sourceOffset), slot(i.destinationOffsetSlot))
    is MemoryInstruction.MemoryCopySsi ->
        copy(i.srcMemory, i.dstMemory, slot(i.bytesToCopySlot), slot(i.sourceOffsetSlot), immediate(i.destinationOffset))
    is MemoryInstruction.MemoryCopySss ->
        copy(i.srcMemory, i.dstMemory, slot(i.bytesToCopySlot), slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot))
    is MemoryInstruction.MemoryFillIii -> BulkMemory.Fill(i.memory, immediate(i.bytesToFill), immediate(i.fillValue), immediate(i.offset))
    is MemoryInstruction.MemoryFillIis -> BulkMemory.Fill(i.memory, immediate(i.bytesToFill), immediate(i.fillValue), slot(i.offsetSlot))
    is MemoryInstruction.MemoryFillIsi -> BulkMemory.Fill(i.memory, immediate(i.bytesToFill), slot(i.fillValueSlot), immediate(i.offset))
    is MemoryInstruction.MemoryFillIss -> BulkMemory.Fill(i.memory, immediate(i.bytesToFill), slot(i.fillValueSlot), slot(i.offsetSlot))
    is MemoryInstruction.MemoryFillSii -> BulkMemory.Fill(i.memory, slot(i.bytesToFillSlot), immediate(i.fillValue), immediate(i.offset))
    is MemoryInstruction.MemoryFillSis -> BulkMemory.Fill(i.memory, slot(i.bytesToFillSlot), immediate(i.fillValue), slot(i.offsetSlot))
    is MemoryInstruction.MemoryFillSsi -> BulkMemory.Fill(i.memory, slot(i.bytesToFillSlot), slot(i.fillValueSlot), immediate(i.offset))
    is MemoryInstruction.MemoryFillSss -> BulkMemory.Fill(i.memory, slot(i.bytesToFillSlot), slot(i.fillValueSlot), slot(i.offsetSlot))
    else -> null
}

private fun copy(
    source: MemoryIndex,
    destination: MemoryIndex,
    count: Operand,
    sourceOffset: Operand,
    destinationOffset: Operand,
) =
    BulkMemory.Copy(source, destination, count, sourceOffset, destinationOffset)
