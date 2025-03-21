package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

fun ReferenceTypeSubstitutor(
    referenceType: ReferenceType,
    substitution: Substitution,
): ReferenceType =
    ReferenceTypeSubstitutor(
        referenceType = referenceType,
        substitution = substitution,
        heapTypeSubstitutor = ::HeapTypeSubstitutor,
    )

internal fun ReferenceTypeSubstitutor(
    referenceType: ReferenceType,
    substitution: Substitution,
    heapTypeSubstitutor: TypeSubstitutor<HeapType>,
): ReferenceType {
    val heapType = heapTypeSubstitutor(referenceType.heapType, substitution)
    return when (referenceType) {
        is ReferenceType.Ref -> ReferenceType.Ref(heapType)
        is ReferenceType.RefNull -> ReferenceType.RefNull(heapType)
    }
}
