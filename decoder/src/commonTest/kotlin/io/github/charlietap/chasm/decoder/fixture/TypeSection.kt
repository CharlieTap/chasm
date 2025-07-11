package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.decoder.section.TypeSection
import io.github.charlietap.chasm.type.DefinedType

internal fun typeSection(
    types: List<Type> = emptyList(),
    definedTypes: List<DefinedType> = emptyList(),
) = TypeSection(
    types = types,
    definedTypes = definedTypes,
)
