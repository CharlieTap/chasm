package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal class WriteKinds private constructor() {

    companion object {
        const val COPIED: Byte = 0
        const val LONG: Byte = 1
        const val INT: Byte = 2
        const val FLOAT: Byte = 3
        const val DOUBLE: Byte = 4
        const val OBJECT: Byte = 5
        const val NARROW_CONSTANT: Byte = 6
        const val WIDE_CONSTANT: Byte = 7

        fun kindOf(type: ValueType): Byte {
            return kindOf(type, objectReferences = false)
        }

        fun kindOf(
            type: ValueType,
            objectReferences: Boolean,
        ): Byte {

            if (objectReferences && type is ValueType.Reference) {
                return OBJECT
            }

            if (type !is ValueType.Number) {
                return LONG
            }

            return when (type.numberType) {
                NumberType.I32 -> INT
                NumberType.F32 -> FLOAT
                NumberType.F64 -> DOUBLE
                else -> LONG
            }
        }

        fun constantKind(bits: Long): Byte {
            return if (bits.toInt().toLong() == bits) NARROW_CONSTANT else WIDE_CONSTANT
        }
    }
}
