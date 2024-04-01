package io.github.charlietap.chasm.executor.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.SubType

fun DefinedTypeUnrollerImpl(
    definedType: DefinedType,
): SubType =
    DefinedTypeUnrollerImpl(
        definedType = definedType,
        recursiveTypeUnroller = ::RecursiveTypeUnrollerImpl,
    )

internal fun DefinedTypeUnrollerImpl(
    definedType: DefinedType,
    recursiveTypeUnroller: RecursiveTypeUnroller,
): SubType {
    val unrolledRecursiveType = recursiveTypeUnroller(definedType.recursiveType)
    return unrolledRecursiveType.subTypes[definedType.recursiveTypeIndex]
}
