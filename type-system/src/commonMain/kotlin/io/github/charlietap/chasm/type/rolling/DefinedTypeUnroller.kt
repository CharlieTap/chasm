package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType

/*
    A DefinedType is a combination of a pointer to the root of a RecursiveType group
    and its index into that group.
    Unrolling effectively chases that pointer, yielding a specific RecursiveType, where
    all of its internal and external references to other RecursiveTypes have been
    replaced with ConcreteDefinedTypes.
 */

typealias DefinedTypeUnroller = (DefinedType) -> SubType

fun DefinedTypeUnroller(
    definedType: DefinedType,
): SubType =
    DefinedTypeUnroller(
        definedType = definedType,
        recursiveTypeUnroller = ::RecursiveTypeUnroller,
    )

internal fun DefinedTypeUnroller(
    definedType: DefinedType,
    recursiveTypeUnroller: RecursiveTypeUnroller,
): SubType {
    val unrolledRecursiveType = recursiveTypeUnroller(definedType.recursiveType)
    return unrolledRecursiveType.subTypes[definedType.recursiveTypeIndex]
}
