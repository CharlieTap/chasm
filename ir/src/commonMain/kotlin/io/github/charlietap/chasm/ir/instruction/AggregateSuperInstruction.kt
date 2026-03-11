package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface AggregateSuperInstruction : Instruction {

    data class ArrayCopy(
        val elementsToCopy: FusedOperand,
        val sourceOffset: FusedOperand,
        val sourceAddress: FusedOperand,
        val destinationOffset: FusedOperand,
        val destinationAddress: FusedOperand,
        val sourceTypeIndex: Index.TypeIndex,
        val destinationTypeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayFill(
        val elementsToFill: FusedOperand,
        val fillValue: FusedOperand,
        val arrayElementOffset: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayGet(
        val address: FusedOperand,
        val field: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayGetSigned(
        val address: FusedOperand,
        val field: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayGetUnsigned(
        val address: FusedOperand,
        val field: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayLen(
        val address: FusedOperand,
        val destination: FusedDestination,
    ) : AggregateSuperInstruction

    data class ArrayNew(
        val size: FusedOperand,
        val value: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayNewDefault(
        val size: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayNewData(
        val sourceOffset: FusedOperand,
        val arrayLength: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val dataIndex: Index.DataIndex,
    ) : AggregateSuperInstruction

    data class ArrayNewElement(
        val sourceOffset: FusedOperand,
        val arrayLength: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val elementIndex: Index.ElementIndex,
    ) : AggregateSuperInstruction

    data class ArrayNewFixed(
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val size: Int,
        val valueSlots: List<Int> = emptyList(),
    ) : AggregateSuperInstruction

    data class ArraySet(
        val value: FusedOperand,
        val field: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class ArrayInitData(
        val elementsToCopy: FusedOperand,
        val sourceOffset: FusedOperand,
        val destinationOffset: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val dataIndex: Index.DataIndex,
    ) : AggregateSuperInstruction

    data class ArrayInitElement(
        val elementsToCopy: FusedOperand,
        val sourceOffset: FusedOperand,
        val destinationOffset: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val elementIndex: Index.ElementIndex,
    ) : AggregateSuperInstruction

    data class RefI31(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : AggregateSuperInstruction

    data class I31GetSigned(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : AggregateSuperInstruction

    data class I31GetUnsigned(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : AggregateSuperInstruction

    data class AnyConvertExtern(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : AggregateSuperInstruction

    data class ExternConvertAny(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : AggregateSuperInstruction

    data class StructGet(
        val address: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : AggregateSuperInstruction

    data class StructGetSigned(
        val address: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : AggregateSuperInstruction

    data class StructGetUnsigned(
        val address: FusedOperand,
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : AggregateSuperInstruction

    data class StructNew(
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
        val fieldSlots: List<Int> = emptyList(),
    ) : AggregateSuperInstruction

    data class StructNewDefault(
        val destination: FusedDestination,
        val typeIndex: Index.TypeIndex,
    ) : AggregateSuperInstruction

    data class StructSet(
        val value: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : AggregateSuperInstruction
}
