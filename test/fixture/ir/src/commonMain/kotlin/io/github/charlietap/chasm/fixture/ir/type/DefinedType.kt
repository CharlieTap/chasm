package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.RecursiveType

fun definedType(
    recursiveType: RecursiveType = recursiveType(),
    recursiveTypeIndex: Int = 0,
) = DefinedType(recursiveType, recursiveTypeIndex)
