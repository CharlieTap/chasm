package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import kotlin.reflect.KClass

internal fun ValueType.asExecutionValue(): KClass<out ExecutionValue> {
    return when (this) {
        is ValueType.Number -> when (this.numberType) {
            NumberType.I32 -> NumberValue.I32::class
            NumberType.I64 -> NumberValue.I64::class
            NumberType.F32 -> NumberValue.F32::class
            NumberType.F64 -> NumberValue.F64::class
        }
        is ValueType.Bottom,
        is ValueType.Reference,
        is ValueType.Vector,
        -> throw IllegalStateException("Cannot convert $this to ExecutionValue")
    }
}

internal fun ValueType.asType(): Type {
    return when (this) {
        is ValueType.Number -> when (this.numberType) {
            NumberType.I32 -> Scalar.Integer
            NumberType.I64 -> Scalar.Long
            NumberType.F32 -> Scalar.Float
            NumberType.F64 -> Scalar.Double
        }
        is ValueType.Bottom,
        is ValueType.Reference,
        is ValueType.Vector,
        -> throw IllegalStateException("Cannot convert $this to Type")
    }
}

internal fun ValueType.isI32() = this == ValueType.Number(NumberType.I32)

internal fun List<ValueType>.matchesStringReturnType() = all { valueType ->
    valueType.isI32()
}
