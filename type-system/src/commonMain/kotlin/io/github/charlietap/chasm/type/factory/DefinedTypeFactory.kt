package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.copy.DeepCopier
import io.github.charlietap.chasm.type.copy.RecursiveTypeDeepCopier
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller

typealias DefinedTypeFactory = (List<RecursiveType>) -> List<DefinedType>

fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
): List<DefinedType> =
    DefinedTypeFactory(
        recursiveTypes = recursiveTypes,
        definedTypeRoller = ::DefinedTypeRoller,
        recursiveTypeCopier = ::RecursiveTypeDeepCopier,
    )

internal fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
    definedTypeRoller: DefinedTypeRoller,
    recursiveTypeCopier: DeepCopier<RecursiveType>,
): List<DefinedType> = recursiveTypes.fold(emptyList()) { acc, recursiveType ->
    val copy = recursiveTypeCopier(recursiveType)
    acc + definedTypeRoller(acc.size, copy)
}
