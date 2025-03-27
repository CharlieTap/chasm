package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType

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

    data class ArrayFill(
        val elementsToFill: LoadOp,
        val fillValue: LoadOp,
        val arrayElementOffset: LoadOp,
        val address: LoadOp,
        val typeIndex: Index.TypeIndex,
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

    data class ArrayNew(
        val size: LoadOp,
        val value: LoadOp,
        val destination: StoreOp,
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : FusedAggregateInstruction

    data class ArrayNewFixed(
        val destination: StoreOp,
        val rtt: RTT,
        val arrayType: ArrayType,
        val size: Int,
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

    data class StructNew(
        val destination: StoreOp,
        val rtt: RTT,
        val structType: StructType,
    ) : FusedAggregateInstruction

    data class StructNewDefault(
        val destination: StoreOp,
        val rtt: RTT,
        val structType: StructType,
        val fields: LongArray,
    ) : FusedAggregateInstruction

    data class StructSet(
        val value: LoadOp,
        val address: LoadOp,
        val fieldIndex: Int,
    ) : FusedAggregateInstruction
}
