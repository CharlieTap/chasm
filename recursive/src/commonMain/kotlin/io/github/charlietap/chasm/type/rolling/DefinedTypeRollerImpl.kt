package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

fun DefinedTypeRollerImpl(
    index: Int,
    recursiveType: RecursiveType,
): List<DefinedType> =
    DefinedTypeRollerImpl(
        index = index,
        recursiveType = recursiveType,
        recursiveTypeRoller = ::RecursiveTypeRollerImpl,
    )

internal fun DefinedTypeRollerImpl(
    index: Int,
    recursiveType: RecursiveType,
    recursiveTypeRoller: RecursiveTypeRoller,
): List<DefinedType> {
    val rolledRecursiveType = recursiveTypeRoller(index, recursiveType)
    return List(rolledRecursiveType.subTypes.size) { subTypeIndex ->
        DefinedType(rolledRecursiveType, subTypeIndex)
    }
}
