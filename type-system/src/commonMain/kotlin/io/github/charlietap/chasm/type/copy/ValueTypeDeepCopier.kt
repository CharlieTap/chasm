package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType

fun ValueTypeDeepCopier(
    input: ValueType,
): ValueType =
    ValueTypeDeepCopier(
        input = input,
        referenceTypeCopier = ::ReferenceTypeDeepCopier,
    )

inline fun ValueTypeDeepCopier(
    input: ValueType,
    referenceTypeCopier: DeepCopier<ReferenceType>,
): ValueType = when (input) {
    is ValueType.Reference -> ValueType.Reference(
        referenceTypeCopier(input.referenceType),
    )
    is ValueType.Bottom,
    is ValueType.Number,
    is ValueType.Vector,
    -> input
}
