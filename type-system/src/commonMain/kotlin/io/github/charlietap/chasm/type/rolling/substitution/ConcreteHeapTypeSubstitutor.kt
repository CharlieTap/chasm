package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType

typealias ConcreteHeapTypeSubstitutor = (ConcreteHeapType) -> ConcreteHeapType

fun TypeIndexToRecursiveTypeIndexSubsitutor(
    lowerBound: Int,
    upperBound: Int,
): ConcreteHeapTypeSubstitutor = { concreteHeapType ->
    when (concreteHeapType) {
        is ConcreteHeapType.TypeIndex -> {
            val typeIndex = concreteHeapType.index.idx.toInt()
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
        is ConcreteHeapType.TypeIndex -> if (concreteHeapType.index.idx.toInt() < types.size) {
            ConcreteHeapType.Defined(types[concreteHeapType.index.idx.toInt()])
        } else {
            concreteHeapType
        }
        else -> concreteHeapType
    }
}
