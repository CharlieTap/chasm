package io.github.charlietap.chasm.gradle.ext

import com.squareup.kotlinpoet.ClassName
import io.github.charlietap.chasm.gradle.Scalar
import io.github.charlietap.chasm.gradle.Type
import io.github.charlietap.chasm.gradle.VALUE_F32_CLASS_NAME
import io.github.charlietap.chasm.gradle.VALUE_F64_CLASS_NAME
import io.github.charlietap.chasm.gradle.VALUE_I32_CLASS_NAME
import io.github.charlietap.chasm.gradle.VALUE_I64_CLASS_NAME
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal fun ValueType.asValue(): ClassName {
    return when (this) {
        is ValueType.Number -> when (this.numberType) {
            NumberType.I32 -> VALUE_I32_CLASS_NAME
            NumberType.I64 -> VALUE_I64_CLASS_NAME
            NumberType.F32 -> VALUE_F32_CLASS_NAME
            NumberType.F64 -> VALUE_F64_CLASS_NAME
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
