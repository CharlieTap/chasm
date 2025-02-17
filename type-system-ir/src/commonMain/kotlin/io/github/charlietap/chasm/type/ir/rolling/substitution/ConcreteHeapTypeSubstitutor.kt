package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType

typealias ConcreteHeapTypeSubstitutor = (ConcreteHeapType) -> ConcreteHeapType

fun TypeIndexToRecursiveTypeIndexSubsitutor(
    lowerBound: Int,
    upperBound: Int,
): ConcreteHeapTypeSubstitutor = { concreteHeapType ->
    when (concreteHeapType) {
        is ConcreteHeapType.TypeIndex -> {
            val typeIndex = concreteHeapType.index.idx
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
        is ConcreteHeapType.TypeIndex -> if (concreteHeapType.index.idx < types.size) {
            ConcreteHeapType.Defined(types[concreteHeapType.index.idx])
        } else {
            concreteHeapType
        }
        else -> concreteHeapType
    }
}
