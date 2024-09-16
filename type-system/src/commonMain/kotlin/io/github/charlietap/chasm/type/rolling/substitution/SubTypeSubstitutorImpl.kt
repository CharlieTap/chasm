package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.SubType

internal fun SubTypeSubstitutorImpl(
    subType: SubType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): SubType =
    SubTypeSubstitutorImpl(
        subType = subType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        heapTypeSubstitutor = ::HeapTypeSubstitutor,
        compositeTypeSubstitutor = ::CompositeTypeSubstitutor,
    )

internal fun SubTypeSubstitutorImpl(
    subType: SubType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    heapTypeSubstitutor: TypeSubstitutor<HeapType>,
    compositeTypeSubstitutor: TypeSubstitutor<CompositeType>,
): SubType = when (subType) {
    is SubType.Open -> subType.copy(
        superTypes = subType.superTypes.map { heapType ->
            heapTypeSubstitutor(heapType, concreteHeapTypeSubstitutor)
        },
        compositeType = subType.compositeType.let { compositeType ->
            compositeTypeSubstitutor(compositeType, concreteHeapTypeSubstitutor)
        },
    )
    is SubType.Final -> subType.copy(
        superTypes = subType.superTypes.map { heapType ->
            heapTypeSubstitutor(heapType, concreteHeapTypeSubstitutor)
        },
        compositeType = subType.compositeType.let { compositeType ->
            compositeTypeSubstitutor(compositeType, concreteHeapTypeSubstitutor)
        },
    )
}
