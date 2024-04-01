package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

fun definedType(
    recursiveType: RecursiveType = recursiveType(),
    recursiveTypeIndex: Int = 0,
) = DefinedType(recursiveType, recursiveTypeIndex)
