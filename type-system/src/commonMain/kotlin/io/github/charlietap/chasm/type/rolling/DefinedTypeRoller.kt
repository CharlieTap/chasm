package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType

/*
    Turn a single recursive type in a list of defined types
    Each DefinedType will contain the same reference to a substituted
    version of the original RecursiveType where its TypeIndexs are now
    RecursiveTypeIndexes which are faster for comparison
 */
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
