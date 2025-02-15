package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.ReferenceType

fun referenceType() = refNullReferenceType()

fun refNullReferenceType(
    heapType: HeapType = heapType(),
) = ReferenceType.RefNull(heapType)

fun refNonNullReferenceType(
    heapType: HeapType = heapType(),
) = ReferenceType.Ref(heapType)
