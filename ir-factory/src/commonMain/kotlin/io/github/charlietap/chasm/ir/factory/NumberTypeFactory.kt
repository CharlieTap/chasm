package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ir.type.NumberType as IRNumberType

internal inline fun NumberTypeFactory(
    numberType: NumberType,
): IRNumberType {
    return when (numberType) {
        NumberType.I32 -> IRNumberType.I32
        NumberType.I64 -> IRNumberType.I64
        NumberType.F32 -> IRNumberType.F32
        NumberType.F64 -> IRNumberType.F64
    }
}
