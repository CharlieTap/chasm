package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.ReferenceType

fun ReferenceTypeSubstitutor(
    referenceType: ReferenceType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ReferenceType =
    ReferenceTypeSubstitutor(
        referenceType = referenceType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        heapTypeSubstitutor = ::HeapTypeSubstitutor,
    )

internal fun ReferenceTypeSubstitutor(
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
