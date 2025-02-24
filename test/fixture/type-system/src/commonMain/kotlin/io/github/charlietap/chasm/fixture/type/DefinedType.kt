package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

fun definedType(
    recursiveType: RecursiveType = recursiveType(),
    recursiveTypeIndex: Int = 0,
) = DefinedType(recursiveType, recursiveTypeIndex)
