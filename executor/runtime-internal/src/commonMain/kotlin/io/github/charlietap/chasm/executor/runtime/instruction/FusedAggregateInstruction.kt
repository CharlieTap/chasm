package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedAggregateInstruction : LinkedInstruction {

    data class ArrayGet(
        val address: LoadOp,
        val field: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayGetSigned(
        val address: LoadOp,
        val field: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArrayGetUnsigned(
        val address: LoadOp,
        val field: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

    data class ArraySet(
        val value: LoadOp,
        val field: LoadOp,
        val address: LoadOp,
        val typeIndex: Index.TypeIndex,
    ) : AggregateInstruction

    data class StructGet(
        val address: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction

    data class StructGetSigned(
        val address: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction

    data class StructGetUnsigned(
        val address: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : FusedAggregateInstruction

    data class StructSet(
        val value: LoadOp,
        val address: LoadOp,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction
}
