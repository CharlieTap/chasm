package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.SubType

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
