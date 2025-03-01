package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedAggregateInstruction : Instruction {

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
}
