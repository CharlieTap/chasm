package io.github.charlietap.chasm.executor.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType

internal fun HeapTypeSubstitutorImpl(
    heapType: HeapType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): HeapType = when (heapType) {
    is ConcreteHeapType -> concreteHeapTypeSubstitutor(heapType)
    else -> heapType
}
