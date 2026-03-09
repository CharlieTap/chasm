package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedAggregateInstruction : Instruction {

    data class ArrayCopy(
        val elementsToCopy: FusedOperand,
        val sourceOffset: FusedOperand,
        val sourceAddress: FusedOperand,
        val destinationOffset: FusedOperand,
        val destinationAddress: FusedOperand,
        val sourceTypeIndex: Index.TypeIndex,
        val destinationTypeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayFill(
        val elementsToFill: FusedOperand,
        val fillValue: FusedOperand,
        val arrayElementOffset: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayGet(
        val address: FusedOperand,
        val field: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayGetSigned(
        val address: FusedOperand,
        val field: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayGetUnsigned(
        val address: FusedOperand,
        val field: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayLen(
        val address: FusedOperand,
        val destination: FusedDestination,
    ) : FusedAggregateInstruction

    data class ArrayNew(
        val size: FusedOperand,
        val value: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayNewDefault(
        val size: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayNewData(
        val sourceOffset: FusedOperand,
        val arrayLength: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val dataIndex: Index.DataIndex,
    ) : FusedAggregateInstruction

    data class ArrayNewElement(
        val sourceOffset: FusedOperand,
        val arrayLength: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val elementIndex: Index.ElementIndex,
    ) : FusedAggregateInstruction

    data class ArrayNewFixed(
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val size: Int,
        val valueSlots: List<Int> = emptyList(),
    ) : FusedAggregateInstruction

    data class ArraySet(
        val value: FusedOperand,
        val field: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayInitData(
        val elementsToCopy: FusedOperand,
        val sourceOffset: FusedOperand,
        val destinationOffset: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val dataIndex: Index.DataIndex,
    ) : FusedAggregateInstruction

    data class ArrayInitElement(
        val elementsToCopy: FusedOperand,
        val sourceOffset: FusedOperand,
        val destinationOffset: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val elementIndex: Index.ElementIndex,
    ) : FusedAggregateInstruction

    data class RefI31(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedAggregateInstruction

    data class I31GetSigned(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedAggregateInstruction

    data class I31GetUnsigned(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedAggregateInstruction

    data class AnyConvertExtern(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedAggregateInstruction

    data class ExternConvertAny(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedAggregateInstruction

    data class StructGet(
        val address: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction

    data class StructGetSigned(
        val address: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction

    data class StructGetUnsigned(
        val address: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction

    data class StructNew(
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldSlots: List<Int> = emptyList(),
    ) : FusedAggregateInstruction

    data class StructNewDefault(
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class StructSet(
        val value: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction
}
