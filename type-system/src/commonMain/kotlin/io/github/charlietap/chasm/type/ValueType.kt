package io.github.charlietap.chasm.type

import kotlin.jvm.JvmInline

sealed interface ValueType : Type {
    @JvmInline
    value class Number(val numberType: NumberType) : ValueType

    @JvmInline
    value class Vector(val vectorType: VectorType) : ValueType

    @JvmInline
    value class Reference(val referenceType: ReferenceType) : ValueType

    @JvmInline
    value class Bottom(val bottomType: BottomType) : ValueType
}
