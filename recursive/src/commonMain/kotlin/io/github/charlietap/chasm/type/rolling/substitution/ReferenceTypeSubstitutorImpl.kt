package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType

fun ReferenceTypeSubstitutorImpl(
    referenceType: ReferenceType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ReferenceType =
    ReferenceTypeSubstitutorImpl(
        referenceType = referenceType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        heapTypeSubstitutor = ::HeapTypeSubstitutorImpl,
    )

internal fun ReferenceTypeSubstitutorImpl(
    referenceType: ReferenceType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    heapTypeSubstitutor: TypeSubstitutor<HeapType>,
): ReferenceType {
    val heapType = heapTypeSubstitutor(referenceType.heapType, concreteHeapTypeSubstitutor)
    return when (referenceType) {
        is ReferenceType.Ref -> ReferenceType.Ref(heapType)
        is ReferenceType.RefNull -> ReferenceType.RefNull(heapType)
    }
}
