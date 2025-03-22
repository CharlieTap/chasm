package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.copy.DeepCopier
import io.github.charlietap.chasm.type.copy.RecursiveTypeDeepCopier
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

typealias DefinedTypeFactory = (List<RecursiveType>) -> List<DefinedType>

fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
): List<DefinedType> =
    DefinedTypeFactory(
        recursiveTypes = recursiveTypes,
        definedTypeRoller = ::DefinedTypeRoller,
        recursiveTypeCopier = ::RecursiveTypeDeepCopier,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun DefinedTypeFactory(
    recursiveTypes: List<RecursiveType>,
    definedTypeRoller: DefinedTypeRoller,
    recursiveTypeCopier: DeepCopier<RecursiveType>,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): List<DefinedType> = buildList {
    val substitution = Substitution.TypeIndexToDefinedType(this)
    for (recursiveType in recursiveTypes) {
        val copy = recursiveTypeCopier(recursiveType)
        val substituted = recursiveTypeSubstitutor(copy, substitution)
        addAll(definedTypeRoller(size, substituted))
    }
}
