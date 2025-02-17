package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.ir.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeIndexToRecursiveTypeIndexSubsitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias RecursiveTypeRoller = (Int, RecursiveType) -> RecursiveType

internal fun RecursiveTypeRoller(
    index: Int,
    recursiveType: RecursiveType,
): RecursiveType =
    RecursiveTypeRoller(
        index = index,
        recursiveType = recursiveType,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    )

internal fun RecursiveTypeRoller(
    index: Int,
    recursiveType: RecursiveType,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
): RecursiveType {
    val upperBound = index + recursiveType.subTypes.size
    val substitutor = TypeIndexToRecursiveTypeIndexSubsitutor(index, upperBound)
    return recursiveTypeSubstitutor(recursiveType, substitutor)
}
