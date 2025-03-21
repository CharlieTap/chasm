package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias RecursiveTypeRoller = (Int, RecursiveType) -> RecursiveType

internal fun RecursiveTypeRoller(
    typeIndex: Int,
    recursiveType: RecursiveType,
): RecursiveType =
    RecursiveTypeRoller(
        typeIndex = typeIndex,
        recursiveType = recursiveType,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun RecursiveTypeRoller(
    typeIndex: Int,
    recursiveType: RecursiveType,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): RecursiveType {
    val upperBound = typeIndex + recursiveType.subTypes.size
    val substitution = Substitution.TypeIndexToRecursiveTypeIndex(typeIndex, upperBound)
    return recursiveTypeSubstitutor(recursiveType, substitution)
}
