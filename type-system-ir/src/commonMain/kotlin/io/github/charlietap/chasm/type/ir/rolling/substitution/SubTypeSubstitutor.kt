package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.SubType

internal fun SubTypeSubstitutor(
    subType: SubType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): SubType =
    SubTypeSubstitutor(
        subType = subType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        heapTypeSubstitutor = ::HeapTypeSubstitutor,
        compositeTypeSubstitutor = ::CompositeTypeSubstitutor,
    )

internal fun SubTypeSubstitutor(
    subType: SubType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    heapTypeSubstitutor: TypeSubstitutor<HeapType>,
    compositeTypeSubstitutor: TypeSubstitutor<CompositeType>,
): SubType = when (subType) {
    is SubType.Open -> subType.apply {
        superTypes = subType.superTypes.map { heapType ->
            heapTypeSubstitutor(heapType, concreteHeapTypeSubstitutor)
        }
        compositeType = subType.compositeType.let { compositeType ->
            compositeTypeSubstitutor(compositeType, concreteHeapTypeSubstitutor)
        }
    }
    is SubType.Final -> subType.apply {
        superTypes = subType.superTypes.map { heapType ->
            heapTypeSubstitutor(heapType, concreteHeapTypeSubstitutor)
        }
        compositeType = subType.compositeType.let { compositeType ->
            compositeTypeSubstitutor(compositeType, concreteHeapTypeSubstitutor)
        }
    }
}
