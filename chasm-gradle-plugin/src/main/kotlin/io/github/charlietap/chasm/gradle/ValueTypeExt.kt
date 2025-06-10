package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal fun ValueType.asType(): Type {
    return when (this) {
        is ValueType.Number -> when(this.numberType) {
            NumberType.I32 -> Scalar.Integer
            NumberType.I64 -> Scalar.Long
            NumberType.F32 -> Scalar.Float
            NumberType.F64 -> Scalar.Double
        }
        is ValueType.Bottom,
        is ValueType.Reference,
        is ValueType.Vector -> throw IllegalStateException("Cannot convert $this to Type")
    }
}

internal fun ValueType.isI32() = this == ValueType.Number(NumberType.I32)

internal fun List<ValueType>.matchesStringReturnType() = all { valueType ->
    valueType.isI32()
}
