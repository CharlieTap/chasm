package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.SubType

fun subType() = openSubType()

fun openSubType(
    heapTypes: List<HeapType> = emptyList(),
    compositeType: CompositeType = compositeType(),
) = SubType.Open(
    heapTypes,
    compositeType,
)

fun finalSubType(
    heapTypes: List<HeapType> = emptyList(),
    compositeType: CompositeType = compositeType(),
) = SubType.Final(
    heapTypes,
    compositeType,
)
