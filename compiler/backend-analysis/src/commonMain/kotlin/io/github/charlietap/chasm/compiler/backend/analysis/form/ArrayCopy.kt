package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.AggregateInstruction

class ArrayCopy(
    val sourceSlot: Int,
    val destinationSlot: Int,
    val count: Operand,
    val sourceOffset: Operand,
    val destinationOffset: Operand,
)

fun AggregateInstruction.arrayCopy(): ArrayCopy? = when (val i = this) {
    is AggregateInstruction.ArrayCopyIii ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, immediate(i.elementsToCopy), immediate(i.sourceOffset), immediate(i.destinationOffset))
    is AggregateInstruction.ArrayCopyIis ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, immediate(i.elementsToCopy), immediate(i.sourceOffset), slot(i.destinationOffsetSlot))
    is AggregateInstruction.ArrayCopyIsi ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, immediate(i.elementsToCopy), slot(i.sourceOffsetSlot), immediate(i.destinationOffset))
    is AggregateInstruction.ArrayCopyIss ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, immediate(i.elementsToCopy), slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot))
    is AggregateInstruction.ArrayCopySii ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, slot(i.elementsToCopySlot), immediate(i.sourceOffset), immediate(i.destinationOffset))
    is AggregateInstruction.ArrayCopySis ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, slot(i.elementsToCopySlot), immediate(i.sourceOffset), slot(i.destinationOffsetSlot))
    is AggregateInstruction.ArrayCopySsi ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, slot(i.elementsToCopySlot), slot(i.sourceOffsetSlot), immediate(i.destinationOffset))
    is AggregateInstruction.ArrayCopySss ->
        ArrayCopy(i.sourceAddressSlot, i.destinationAddressSlot, slot(i.elementsToCopySlot), slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot))
    else -> null
}
