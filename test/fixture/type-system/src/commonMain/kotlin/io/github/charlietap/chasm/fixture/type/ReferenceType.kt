package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

fun referenceType() = refNullReferenceType()

fun refNullReferenceType(
    heapType: HeapType = heapType(),
) = ReferenceType.RefNull(heapType)

fun refNonNullReferenceType(
    heapType: HeapType = heapType(),
) = ReferenceType.Ref(heapType)
