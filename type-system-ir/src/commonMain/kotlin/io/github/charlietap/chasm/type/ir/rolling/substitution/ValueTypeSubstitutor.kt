package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.ReferenceType
import io.github.charlietap.chasm.ir.type.ValueType

internal fun ValueTypeSubstitutor(
    valueType: ValueType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ValueType =
    ValueTypeSubstitutor(
        valueType = valueType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
    )

internal fun ValueTypeSubstitutor(
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
