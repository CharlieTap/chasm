package io.github.charlietap.chasm.type.ir.copy

import io.github.charlietap.chasm.ir.type.ReferenceType
import io.github.charlietap.chasm.ir.type.ValueType

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
