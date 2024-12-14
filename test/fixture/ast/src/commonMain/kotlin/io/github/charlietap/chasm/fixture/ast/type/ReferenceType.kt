package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType

fun referenceType() = refNullReferenceType()

fun refNullReferenceType(
    heapType: HeapType = heapType(),
) = ReferenceType.RefNull(heapType)

fun refNonNullReferenceType(
    heapType: HeapType = heapType(),
) = ReferenceType.Ref(heapType)
