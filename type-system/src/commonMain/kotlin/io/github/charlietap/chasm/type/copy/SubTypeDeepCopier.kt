package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.SubType

fun SubTypeDeepCopier(
    input: SubType,
): SubType =
    SubTypeDeepCopier(
        input = input,
        compositeTypeCopier = ::CompositeTypeDeepCopier,
        heapTypeCopier = ::HeapTypeDeepCopier,
    )

inline fun SubTypeDeepCopier(
    input: SubType,
    compositeTypeCopier: DeepCopier<CompositeType>,
    heapTypeCopier: DeepCopier<HeapType>,
): SubType = when (input) {
    is SubType.Open -> input.copy(
        compositeType = compositeTypeCopier(input.compositeType),
        superTypes = input.superTypes.map(heapTypeCopier),
    )
    is SubType.Final -> input.copy(
        compositeType = compositeTypeCopier(input.compositeType),
        superTypes = input.superTypes.map(heapTypeCopier),
    )
}