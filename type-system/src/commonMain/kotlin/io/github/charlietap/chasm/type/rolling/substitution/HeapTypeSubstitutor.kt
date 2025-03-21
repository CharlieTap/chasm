package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType

internal fun HeapTypeSubstitutor(
    heapType: HeapType,
    substitution: Substitution,
): HeapType = when (heapType) {
    is ConcreteHeapType.TypeIndex -> substitution.concreteHeapTypeSubstitutor(heapType)
    is ConcreteHeapType.RecursiveTypeIndex -> substitution.concreteHeapTypeSubstitutor(heapType)
    else -> heapType
}
