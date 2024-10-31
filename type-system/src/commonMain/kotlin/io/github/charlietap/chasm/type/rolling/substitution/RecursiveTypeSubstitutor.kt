package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType

fun RecursiveTypeSubstitutor(
    recursiveType: RecursiveType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): RecursiveType =
    RecursiveTypeSubstitutor(
        recursiveType = recursiveType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        subTypeSubstitutor = ::SubTypeSubstitutorImpl,
    )

internal fun RecursiveTypeSubstitutor(
    recursiveType: RecursiveType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    subTypeSubstitutor: TypeSubstitutor<SubType>,
): RecursiveType = RecursiveType(
    subTypes = recursiveType.subTypes.map { subType ->
        subTypeSubstitutor(subType, concreteHeapTypeSubstitutor)
    },
)
