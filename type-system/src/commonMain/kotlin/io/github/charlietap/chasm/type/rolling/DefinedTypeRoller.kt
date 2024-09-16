package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

typealias DefinedTypeRoller = (Int, RecursiveType) -> List<DefinedType>

fun DefinedTypeRoller(
    index: Int,
    recursiveType: RecursiveType,
): List<DefinedType> =
    DefinedTypeRoller(
        index = index,
        recursiveType = recursiveType,
        recursiveTypeRoller = ::RecursiveTypeRoller,
    )

internal fun DefinedTypeRoller(
    index: Int,
    recursiveType: RecursiveType,
    recursiveTypeRoller: RecursiveTypeRoller,
): List<DefinedType> {
    val rolledRecursiveType = recursiveTypeRoller(index, recursiveType)
    return List(rolledRecursiveType.subTypes.size) { subTypeIndex ->
        DefinedType(rolledRecursiveType, subTypeIndex)
    }
}
