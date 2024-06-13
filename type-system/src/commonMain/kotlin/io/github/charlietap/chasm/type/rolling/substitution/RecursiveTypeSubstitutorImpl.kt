package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType

fun RecursiveTypeSubstitutorImpl(
    recursiveType: RecursiveType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): RecursiveType =
    RecursiveTypeSubstitutorImpl(
        recursiveType = recursiveType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        subTypeSubstitutor = ::SubTypeSubstitutorImpl,
    )

internal fun RecursiveTypeSubstitutorImpl(
    recursiveType: RecursiveType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    subTypeSubstitutor: TypeSubstitutor<SubType>,
): RecursiveType = RecursiveType(
    recursiveType.subTypes.map { subType ->
        subTypeSubstitutor(subType, concreteHeapTypeSubstitutor)
    },
)
