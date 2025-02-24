package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType

fun RecursiveTypeDeepCopier(
    input: RecursiveType,
): RecursiveType =
    RecursiveTypeDeepCopier(
        input = input,
        subTypeCopier = ::SubTypeDeepCopier,
    )

inline fun RecursiveTypeDeepCopier(
    input: RecursiveType,
    subTypeCopier: DeepCopier<SubType>,
): RecursiveType = input.copy(
    subTypes = input.subTypes.map(subTypeCopier),
    state = input.state,
)
