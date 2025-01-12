package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller

typealias DefinedTypeFactory = (List<RecursiveType>) -> List<DefinedType>

fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
): List<DefinedType> =
    DefinedTypeFactory(
        recursiveTypes = recursiveTypes,
        definedTypeRoller = ::DefinedTypeRoller,
    )

internal fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
    definedTypeRoller: DefinedTypeRoller,
): List<DefinedType> = recursiveTypes.fold(emptyList()) { acc, recursiveType ->
    acc + definedTypeRoller(acc.size, recursiveType)
}
