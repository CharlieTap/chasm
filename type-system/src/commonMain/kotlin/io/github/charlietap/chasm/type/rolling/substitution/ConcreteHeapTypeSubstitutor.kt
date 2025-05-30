package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

typealias ConcreteHeapTypeSubstitutor = (ConcreteHeapType) -> ConcreteHeapType

fun TypeIndexToRecursiveTypeIndexSubsitutor(
    lowerBound: Int,
    upperBound: Int,
): ConcreteHeapTypeSubstitutor = { concreteHeapType ->
    when (concreteHeapType) {
        is ConcreteHeapType.TypeIndex -> {
            val typeIndex = concreteHeapType.index
            if (typeIndex in lowerBound..<upperBound) {
                ConcreteHeapType.RecursiveTypeIndex(typeIndex - lowerBound)
            } else {
                concreteHeapType
            }
        }
        else -> concreteHeapType
    }
}

fun TypeIndexToDefinedTypeSubstitutor(
    types: List<DefinedType>,
): ConcreteHeapTypeSubstitutor = { concreteHeapType ->
    when (concreteHeapType) {
        is ConcreteHeapType.TypeIndex -> if (concreteHeapType.index < types.size) {
            ConcreteHeapType.Defined(types[concreteHeapType.index])
        } else {
            concreteHeapType
        }
        else -> concreteHeapType
    }
}

fun RecursiveTypeIndexToDefinedTypeSubstitutor(
    recursiveType: RecursiveType,
): ConcreteHeapTypeSubstitutor = { heapType ->
    when (heapType) {
        is ConcreteHeapType.RecursiveTypeIndex -> ConcreteHeapType.Defined(
            DefinedType(recursiveType, heapType.index),
        )
        else -> heapType
    }
}
