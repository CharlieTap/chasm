package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType
import kotlin.jvm.JvmInline

sealed interface AggregateInstruction : LinkedInstruction {

    data class StructNew(
        val rtt: RTT,
        val structType: StructType,
    ) : AggregateInstruction

    data class StructNewDefault(
        val rtt: RTT,
        val structType: StructType,
        val fields: LongArray,
    ) : AggregateInstruction

    data class StructGet(val typeIndex: Index.TypeIndex, val fieldIndex: Index.FieldIndex) : AggregateInstruction

    data class StructGetSigned(val typeIndex: Index.TypeIndex, val fieldIndex: Index.FieldIndex) : AggregateInstruction

    data class StructGetUnsigned(val typeIndex: Index.TypeIndex, val fieldIndex: Index.FieldIndex) : AggregateInstruction

    @JvmInline
    value class StructSet(
        val fieldIndex: Int,
    ) : AggregateInstruction

    data class ArrayNew(
        val rtt: RTT,
        val arrayType: ArrayType,
    ) : AggregateInstruction

    data class ArrayNewFixed(
        val rtt: RTT,
        val arrayType: ArrayType,
        val length: UInt,
    ) : AggregateInstruction

    data class ArrayNewDefault(
        val rtt: RTT,
        val arrayType: ArrayType,
        val field: Long,
    ) : AggregateInstruction

    data class ArrayNewData(
        val rtt: RTT,
        val arrayType: ArrayType,
        val dataInstance: DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction

    data class ArrayNewElement(
        val rtt: RTT,
        val arrayType: ArrayType,
        val elementInstance: ElementInstance,
    ) : AggregateInstruction

    @JvmInline
    value class ArrayGet(val typeIndex: Index.TypeIndex) : AggregateInstruction

    @JvmInline
    value class ArrayGetSigned(val typeIndex: Index.TypeIndex) : AggregateInstruction

    @JvmInline
    value class ArrayGetUnsigned(val typeIndex: Index.TypeIndex) : AggregateInstruction

    @JvmInline
    value class ArraySet(val typeIndex: Index.TypeIndex) : AggregateInstruction

    data object ArrayLen : AggregateInstruction

    @JvmInline
    value class ArrayFill(val typeIndex: Index.TypeIndex) : AggregateInstruction

    data class ArrayCopy(val sourceTypeIndex: Index.TypeIndex, val destinationTypeIndex: Index.TypeIndex) : AggregateInstruction

    data class ArrayInitData(
        val typeIndex: Index.TypeIndex,
        val dataInstance: DataInstance,
        val fieldWidthInBytes: Int,
    ) : AggregateInstruction

    data class ArrayInitElement(
        val typeIndex: Index.TypeIndex,
        val elementInstance: ElementInstance,
    ) : AggregateInstruction

    data object RefI31 : AggregateInstruction

    data object I31GetSigned : AggregateInstruction

    data object I31GetUnsigned : AggregateInstruction

    data object AnyConvertExtern : AggregateInstruction

    data object ExternConvertAny : AggregateInstruction
}
