package io.github.charlietap.chasm.compiled.corpus.value

import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal fun NumberValue<*>.bits(): Long = when (this) {
    is NumberValue.I32 -> value.toLong()
    is NumberValue.I64 -> value
    is NumberValue.F32 -> value.toRawBits().toLong()
    is NumberValue.F64 -> value.toRawBits()
}

internal fun ExecutionValue.bits(): Long = (this as? NumberValue<*>)?.bits()
    ?: error("Expected a number but found $this")

internal fun ReferenceValue.reference(): Any? = when (this) {
    is ReferenceValue.Null -> null
    is ReferenceValue.Struct -> reference
    is ReferenceValue.Array -> reference
    is ReferenceValue.I31 -> reference
    is ReferenceValue.Function -> reference
    is ReferenceValue.Exception -> reference
    is ReferenceValue.Extern -> value
}

internal fun numberValue(
    bits: Long,
    type: ValueType.Number,
): NumberValue<*> = when (type.numberType) {
    NumberType.I32 -> NumberValue.I32(bits.toInt())
    NumberType.I64 -> NumberValue.I64(bits)
    NumberType.F32 -> NumberValue.F32(Float.fromBits(bits.toInt()))
    NumberType.F64 -> NumberValue.F64(Double.fromBits(bits))
}

internal fun referenceValue(
    reference: Any?,
    heapType: HeapType,
): ReferenceValue = when (reference) {
    null -> ReferenceValue.Null(heapType)
    is StructReference -> ReferenceValue.Struct(reference)
    is ArrayReference -> ReferenceValue.Array(reference)
    is I31Reference -> ReferenceValue.I31(reference)
    is FunctionReference -> ReferenceValue.Function(reference)
    is ExceptionReference -> ReferenceValue.Exception(reference)
    else -> ReferenceValue.Extern(reference)
}

internal fun ExecutionValue.matches(
    other: ExecutionValue,
): Boolean = when (this) {
    is NumberValue<*> -> other is NumberValue<*> && bits() == other.bits()
    is ReferenceValue -> this == other
    is VectorValue.V128 -> other is VectorValue.V128 && v128.contentEquals(other.v128)
}

internal fun ExecutionValue.describe(): String = when (this) {
    is NumberValue<*> -> "bits=${bits()}"
    is ReferenceValue -> toString()
    is VectorValue.V128 -> "v128=${v128.toHex()}"
}
