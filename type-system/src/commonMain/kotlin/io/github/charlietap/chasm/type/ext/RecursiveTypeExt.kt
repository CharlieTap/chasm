package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

inline fun RecursiveType.definedType(): DefinedType = DefinedType(
    typeIndex = Int.MIN_VALUE,
    recursiveType = this,
    recursiveTypeIndex = 0,
)
