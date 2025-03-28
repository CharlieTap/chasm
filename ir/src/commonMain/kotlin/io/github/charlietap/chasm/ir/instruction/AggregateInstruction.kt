package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import kotlin.jvm.JvmInline

sealed interface AggregateInstruction : Instruction {

    @JvmInline
    value class StructNew(val typeIndex: Index.TypeIndex) : AggregateInstruction

    @JvmInline
    value class StructNewDefault(val typeIndex: Index.TypeIndex) : AggregateInstruction

    data class StructGet(val typeIndex: Index.TypeIndex, val fieldIndex: Index.FieldIndex) : AggregateInstruction

    data class StructGetSigned(val typeIndex: Index.TypeIndex, val fieldIndex: Index.FieldIndex) : AggregateInstruction

    data class StructGetUnsigned(val typeIndex: Index.TypeIndex, val fieldIndex: Index.FieldIndex) : AggregateInstruction

    data class StructSet(
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : AggregateInstruction

    @JvmInline
    value class ArrayNew(val typeIndex: Index.TypeIndex) : AggregateInstruction

    data class ArrayNewFixed(val typeIndex: Index.TypeIndex, val size: UInt) : AggregateInstruction

    @JvmInline
    value class ArrayNewDefault(val typeIndex: Index.TypeIndex) : AggregateInstruction

    data class ArrayNewData(val typeIndex: Index.TypeIndex, val dataIndex: Index.DataIndex) : AggregateInstruction

    data class ArrayNewElement(val typeIndex: Index.TypeIndex, val elementIndex: Index.ElementIndex) : AggregateInstruction

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

    data class ArrayInitData(val typeIndex: Index.TypeIndex, val dataIndex: Index.DataIndex) : AggregateInstruction

    data class ArrayInitElement(val typeIndex: Index.TypeIndex, val elementIndex: Index.ElementIndex) : AggregateInstruction

    data object RefI31 : AggregateInstruction

    data object I31GetSigned : AggregateInstruction

    data object I31GetUnsigned : AggregateInstruction

    data object AnyConvertExtern : AggregateInstruction

    data object ExternConvertAny : AggregateInstruction
}
