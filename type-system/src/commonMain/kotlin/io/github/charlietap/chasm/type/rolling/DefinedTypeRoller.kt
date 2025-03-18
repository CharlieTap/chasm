package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

/*
    Turn a single recursive type in a list of defined types
    Each DefinedType will contain the same reference to a substituted
    version of the original RecursiveType where its TypeIndexs are now
    RecursiveTypeIndexes which are faster for comparison
 */
typealias DefinedTypeRoller = (Int, RecursiveType) -> List<DefinedType>

fun DefinedTypeRoller(
    typeIndex: Int,
    recursiveType: RecursiveType,
): List<DefinedType> =
    DefinedTypeRoller(
        typeIndex = typeIndex,
        recursiveType = recursiveType,
        recursiveTypeRoller = ::RecursiveTypeRoller,
    )

internal fun DefinedTypeRoller(
    typeIndex: Int,
    recursiveType: RecursiveType,
    recursiveTypeRoller: RecursiveTypeRoller,
): List<DefinedType> {
    val rolledRecursiveType = recursiveTypeRoller(typeIndex, recursiveType)
    return List(rolledRecursiveType.subTypes.size) { subTypeIndex ->
        DefinedType(typeIndex, rolledRecursiveType, subTypeIndex)
    }
}
