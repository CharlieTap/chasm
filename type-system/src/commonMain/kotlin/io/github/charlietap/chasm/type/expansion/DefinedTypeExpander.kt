package io.github.charlietap.chasm.type.expansion

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType

typealias DefinedTypeExpander = (DefinedType) -> CompositeType

fun DefinedTypeExpander(
    definedType: DefinedType,
): CompositeType = definedType.asSubType.compositeType
