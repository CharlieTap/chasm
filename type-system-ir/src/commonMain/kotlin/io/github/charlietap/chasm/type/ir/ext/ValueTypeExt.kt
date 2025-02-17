package io.github.charlietap.chasm.type.ir.ext

import io.github.charlietap.chasm.ir.type.NumberType
import io.github.charlietap.chasm.ir.type.ValueType

inline fun ValueType.bitWidth() = when (this) {
    is ValueType.Number -> when (this.numberType) {
        NumberType.I32,
        NumberType.F32,
        -> 32
        NumberType.I64,
        NumberType.F64,
        -> 64
    }
    is ValueType.Vector -> 128
    is ValueType.Reference,
    is ValueType.Bottom,
    -> null
}
