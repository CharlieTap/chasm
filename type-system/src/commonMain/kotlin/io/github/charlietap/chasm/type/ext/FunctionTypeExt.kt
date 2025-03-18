package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller

inline fun FunctionType.recursiveType() = RecursiveType(
    subTypes = listOf(
        SubType.Final(emptyList(), CompositeType.Function(this)),
    ),
    state = RecursiveType.STATE_CLOSED,
)

inline fun FunctionType.definedType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
): DefinedType = definedTypeRoller(Int.MIN_VALUE, this.recursiveType()).first()
