package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType

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
