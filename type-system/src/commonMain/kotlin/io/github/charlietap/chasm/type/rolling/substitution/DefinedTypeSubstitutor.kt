package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType

fun DefinedTypeSubstitutor(
    definedType: DefinedType,
    substitution: Substitution,
): DefinedType =
    DefinedTypeSubstitutor(
        definedType = definedType,
        substitution = substitution,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun DefinedTypeSubstitutor(
    definedType: DefinedType,
    substitution: Substitution,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): DefinedType = definedType.apply {
    recursiveType = recursiveTypeSubstitutor(definedType.recursiveType, substitution)
}
