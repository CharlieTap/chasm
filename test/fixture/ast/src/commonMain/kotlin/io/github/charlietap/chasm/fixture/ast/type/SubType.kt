package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.SubType

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
