package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedAggregateInstruction : Instruction {

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

    data class ArraySet(
        val value: FusedOperand,
        val field: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
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

    data class StructSet(
        val value: FusedOperand,
        val address: FusedOperand,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction
}
