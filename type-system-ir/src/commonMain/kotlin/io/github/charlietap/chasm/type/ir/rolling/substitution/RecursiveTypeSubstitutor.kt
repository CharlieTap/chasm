package io.github.charlietap.chasm.type.ir.rolling.substitution

import io.github.charlietap.chasm.ir.type.RecursiveType
import io.github.charlietap.chasm.ir.type.SubType

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
) = recursiveType.apply {
    subTypes = recursiveType.subTypes.map { subType ->
        subTypeSubstitutor(subType, concreteHeapTypeSubstitutor)
    }
    state = when (recursiveType.state) {
        RecursiveType.STATE_SUBSTITUTED,
        RecursiveType.STATE_CLOSED,
        -> RecursiveType.STATE_CLOSED

        else -> RecursiveType.STATE_SUBSTITUTED
    }
}
