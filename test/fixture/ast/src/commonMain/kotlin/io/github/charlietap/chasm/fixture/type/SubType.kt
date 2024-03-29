package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.SubType

fun subType() = openSubType()

fun openSubType(
    typeIndices: List<Index.TypeIndex> = emptyList(),
    compositeType: CompositeType = compositeType(),
) = SubType.Open(
    typeIndices,
    compositeType,
)

fun finalSubType(
    typeIndices: List<Index.TypeIndex> = emptyList(),
    compositeType: CompositeType = compositeType(),
) = SubType.Final(
    typeIndices,
    compositeType,
)
