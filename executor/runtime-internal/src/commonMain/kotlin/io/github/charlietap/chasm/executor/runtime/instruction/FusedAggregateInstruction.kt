package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedAggregateInstruction : LinkedInstruction {

    data class ArrayCopy(
        val elementsToCopy: LoadOp,
        val sourceOffset: LoadOp,
        val sourceAddress: LoadOp,
        val destinationOffset: LoadOp,
        val destinationAddress: LoadOp,
        val sourceTypeIndex: Index.TypeIndex,
        val destinationTypeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

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

    data class ArrayLen(
        val address: LoadOp,
        val destination: StoreOp,
    ) : FusedAggregateInstruction

    data class ArraySet(
        val value: LoadOp,
        val field: LoadOp,
        val address: LoadOp,
        val typeIndex: Index.TypeIndex,
    ) : FusedAggregateInstruction

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
