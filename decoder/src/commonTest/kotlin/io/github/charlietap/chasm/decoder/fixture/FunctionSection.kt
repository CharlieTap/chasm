package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.section.function.FunctionHeader
import io.github.charlietap.chasm.decoder.section.FunctionSection
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex

internal fun functionHeader(
    idx: Index.FunctionIndex = functionIndex(),
    typeIndex: Index.TypeIndex = typeIndex(),
) = FunctionHeader(
    idx = idx,
    typeIndex = typeIndex,
)

internal fun functionSection(
    headers: List<FunctionHeader> = emptyList(),
) = FunctionSection(headers)
