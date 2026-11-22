package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.runtime.value.VectorValue
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

internal fun argumentMismatch(
    type: FunctionType,
    args: List<ExecutionValue>,
): String? {

    val params = type.params.types
    if (args.size != params.size) {
        return "ArgumentCountMismatch(expected=${params.size}, actual=${args.size})"
    }

    val index = params.indices.firstOrNull { index -> !matchesType(params[index], args[index]) } ?: return null

    return "ArgumentTypeMismatch(index=$index, expected=${params[index]}, actual=${args[index]})"
}

internal fun matchesType(
    type: ValueType,
    value: ExecutionValue,
): Boolean {
    return when (type) {
        is ValueType.Number -> when (type.numberType) {
            NumberType.I32 -> value is NumberValue.I32
            NumberType.I64 -> value is NumberValue.I64
            NumberType.F32 -> value is NumberValue.F32
            NumberType.F64 -> value is NumberValue.F64
        }
        is ValueType.Vector -> value is VectorValue.V128
        is ValueType.Reference -> {
            value is ReferenceValue && (type.referenceType is ReferenceType.RefNull || value !is ReferenceValue.Null)
        }
        is ValueType.Bottom -> false
    }
}
