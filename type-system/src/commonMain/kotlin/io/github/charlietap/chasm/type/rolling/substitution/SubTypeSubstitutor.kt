package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.SubType

internal fun SubTypeSubstitutor(
    subType: SubType,
    substitution: Substitution,
): SubType =
    SubTypeSubstitutor(
        subType = subType,
        substitution = substitution,
        heapTypeSubstitutor = ::HeapTypeSubstitutor,
        compositeTypeSubstitutor = ::CompositeTypeSubstitutor,
    )

internal fun SubTypeSubstitutor(
    subType: SubType,
    substitution: Substitution,
    heapTypeSubstitutor: TypeSubstitutor<HeapType>,
    compositeTypeSubstitutor: TypeSubstitutor<CompositeType>,
): SubType = when (subType) {
    is SubType.Open -> subType.apply {
        superTypes = subType.superTypes.map { heapType ->
            heapTypeSubstitutor(heapType, substitution)
        }
        compositeType = compositeTypeSubstitutor(subType.compositeType, substitution)
    }
    is SubType.Final -> subType.apply {
        superTypes = subType.superTypes.map { heapType ->
            heapTypeSubstitutor(heapType, substitution)
        }
        compositeType = compositeTypeSubstitutor(subType.compositeType, substitution)
    }
}
