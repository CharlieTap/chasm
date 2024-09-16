package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.SubType

internal typealias DefinedTypeUnroller = (DefinedType) -> SubType

internal fun DefinedTypeUnroller(
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
