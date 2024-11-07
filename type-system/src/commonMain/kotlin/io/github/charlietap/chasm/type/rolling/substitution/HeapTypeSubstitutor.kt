package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType

internal fun HeapTypeSubstitutor(
    heapType: HeapType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): HeapType = when (heapType) {
    is ConcreteHeapType.TypeIndex -> concreteHeapTypeSubstitutor(heapType)
    is ConcreteHeapType.RecursiveTypeIndex -> concreteHeapTypeSubstitutor(heapType)
    else -> heapType
}
