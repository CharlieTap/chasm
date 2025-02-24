package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.Type
import io.github.charlietap.chasm.type.RecursiveType

fun type(
    idx: Index.TypeIndex = typeIndex(),
    recursiveType: RecursiveType = recursiveType(),
) = Type(
    idx = idx,
    recursiveType = recursiveType,
)
