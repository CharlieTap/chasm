package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller

inline fun FunctionType.recursiveType() = RecursiveType(
    subTypes = listOf(
        SubType.Final(emptyList(), CompositeType.Function(this)),
    ),
    state = RecursiveType.STATE_CLOSED,
)

inline fun FunctionType.definedType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
): DefinedType = definedTypeRoller(0, this.recursiveType()).first()
