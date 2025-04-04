package io.github.charlietap.chasm.type.rolling

import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.copy.DeepCopier
import io.github.charlietap.chasm.type.copy.RecursiveTypeDeepCopier
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

typealias RecursiveTypeUnroller = (RecursiveType) -> RecursiveType

fun RecursiveTypeUnroller(
    recursiveType: RecursiveType,
): RecursiveType =
    RecursiveTypeUnroller(
        recursiveType = recursiveType,
        recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
        recursiveTypeDeepCopier = ::RecursiveTypeDeepCopier,
    )

internal fun RecursiveTypeUnroller(
    recursiveType: RecursiveType,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
    recursiveTypeDeepCopier: DeepCopier<RecursiveType>,
): RecursiveType {
    val substitution = Substitution.RecursiveTypeIndexToDefinedType(recursiveType)
    // Its important we copy when making this version as part of rolling,
    // else we will alter the type in the context, and it's outer defined type
    // will now contain a recursive type which includes a reference to itself
    val copy = recursiveTypeDeepCopier(recursiveType)
    return recursiveTypeSubstitutor(copy, substitution)
}
