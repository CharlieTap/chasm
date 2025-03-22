package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType

inline fun FunctionType.recursiveType() = RecursiveType(
    subTypes = listOf(
        SubType.Final(emptyList(), CompositeType.Function(this)),
    ),
    state = RecursiveType.State.CLOSED,
)

inline fun FunctionType.definedType(): DefinedType = DefinedType(
    recursiveType = recursiveType(),
    recursiveTypeIndex = 0,
)
