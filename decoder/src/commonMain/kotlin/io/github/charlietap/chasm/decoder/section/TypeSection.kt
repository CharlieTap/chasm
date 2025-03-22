package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.type.DefinedType

internal data class TypeSection(
    val types: List<Type>,
    val definedTypes: List<DefinedType>,
) : Section
