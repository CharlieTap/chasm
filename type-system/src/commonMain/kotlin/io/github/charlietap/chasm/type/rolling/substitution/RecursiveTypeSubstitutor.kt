package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType

fun RecursiveTypeSubstitutor(
    recursiveType: RecursiveType,
    substitution: Substitution,
): RecursiveType =
    RecursiveTypeSubstitutor(
        recursiveType = recursiveType,
        substitution = substitution,
        subTypeSubstitutor = ::SubTypeSubstitutor,
    )

internal fun RecursiveTypeSubstitutor(
    recursiveType: RecursiveType,
    substitution: Substitution,
    subTypeSubstitutor: TypeSubstitutor<SubType>,
) = recursiveType.apply {
    subTypes = recursiveType.subTypes.map { subType ->
        subTypeSubstitutor(subType, substitution)
    }
    state = when {
        state == RecursiveType.State.INTERNAL_SUBSTITUTED && substitution.outputState == RecursiveType.State.EXTERNAL_SUBSTITUTED -> RecursiveType.State.CLOSED
        state == RecursiveType.State.EXTERNAL_SUBSTITUTED && substitution.outputState == RecursiveType.State.INTERNAL_SUBSTITUTED -> RecursiveType.State.CLOSED
        else -> substitution.outputState
    }
}
