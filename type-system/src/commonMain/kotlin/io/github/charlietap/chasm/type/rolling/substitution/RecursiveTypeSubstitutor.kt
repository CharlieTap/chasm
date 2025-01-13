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
        subTypeSubstitutor = ::SubTypeSubstitutor,
    )

internal fun RecursiveTypeSubstitutor(
    recursiveType: RecursiveType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    subTypeSubstitutor: TypeSubstitutor<SubType>,
) = RecursiveType(
    subTypes = recursiveType.subTypes.map { subType ->
        subTypeSubstitutor(subType, concreteHeapTypeSubstitutor)
    },
    state = when (recursiveType.state) {
        RecursiveType.STATE_SUBSTITUTED,
        RecursiveType.STATE_CLOSED,
        -> RecursiveType.STATE_CLOSED
        else -> RecursiveType.STATE_SUBSTITUTED
    },
)
