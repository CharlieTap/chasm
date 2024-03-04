package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.fixture.type.functionType

fun type(
    idx: Index.TypeIndex = Index.TypeIndex(0u),
    functionType: FunctionType = functionType(),
) = Type(
    idx = idx,
    functionType = functionType,
)
