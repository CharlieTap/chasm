package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

internal fun ValueTypeSubstitutor(
    valueType: ValueType,
    substitution: Substitution,
): ValueType =
    ValueTypeSubstitutor(
        valueType = valueType,
        substitution = substitution,
        referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
    )

internal fun ValueTypeSubstitutor(
    valueType: ValueType,
    substitution: Substitution,
    referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
): ValueType = when (valueType) {
    is ValueType.Number,
    is ValueType.Vector,
    is ValueType.Bottom,
    -> valueType
    is ValueType.Reference -> {
        ValueType.Reference(
            referenceTypeSubstitutor(
                valueType.referenceType,
                substitution,
            ),
        )
    }
}
