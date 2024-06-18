package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType

internal fun ValueTypeSubstitutorImpl(
    valueType: ValueType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ValueType =
    ValueTypeSubstitutorImpl(
        valueType = valueType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        referenceTypeSubstitutor = ::ReferenceTypeSubstitutorImpl,
    )

internal fun ValueTypeSubstitutorImpl(
    valueType: ValueType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
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
                concreteHeapTypeSubstitutor,
            ),
        )
    }
}
