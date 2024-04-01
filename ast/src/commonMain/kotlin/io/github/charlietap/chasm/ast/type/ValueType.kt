package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

sealed interface ValueType : BottomType {
    @JvmInline
    value class Number(val numberType: NumberType) : ValueType

    @JvmInline
    value class Vector(val vectorType: VectorType) : ValueType

    @JvmInline
    value class Reference(val referenceType: ReferenceType) : ValueType
}
