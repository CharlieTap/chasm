package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

internal fun ReferenceTypeDeepCopier(
    input: ReferenceType,
): ReferenceType =
    ReferenceTypeDeepCopier(
        input = input,
        heapTypeCopier = ::HeapTypeDeepCopier,
    )

internal inline fun ReferenceTypeDeepCopier(
    input: ReferenceType,
    heapTypeCopier: DeepCopier<HeapType>,
): ReferenceType = when (input) {
    is ReferenceType.Ref -> ReferenceType.Ref(
        heapTypeCopier(input.heapType),
    )
    is ReferenceType.RefNull -> ReferenceType.RefNull(
        heapTypeCopier(input.heapType),
    )
}
