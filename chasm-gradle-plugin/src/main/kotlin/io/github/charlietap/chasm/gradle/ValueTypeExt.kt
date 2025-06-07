package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal fun ValueType.isI32() = this == ValueType.Number(NumberType.I32)

internal fun ValueType.asTypeName(): TypeName {
    return when (this) {
        is ValueType.Number -> when(this.numberType) {
            NumberType.I32 -> INT
            NumberType.I64 -> LONG
            NumberType.F32 -> FLOAT
            NumberType.F64 -> DOUBLE
        }
        is ValueType.Bottom,
        is ValueType.Reference,
        is ValueType.Vector -> this::class.asTypeName()
    }
}

internal fun List<ValueType>.matchesStringReturnType() = all { valueType ->
    valueType.isI32()
}
