package io.github.charlietap.chasm.type.ir.factory

import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.RecursiveType
import io.github.charlietap.chasm.type.ir.copy.DeepCopier
import io.github.charlietap.chasm.type.ir.copy.RecursiveTypeDeepCopier
import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeRoller

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
