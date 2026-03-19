package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.ValueType

fun GlobalTypeSubstitutor(
    globalType: GlobalType,
    substitution: Substitution,
): GlobalType =
    GlobalTypeSubstitutor(
        globalType = globalType,
        substitution = substitution,
        valueTypeSubstitutor = ::ValueTypeSubstitutor,
    )

internal fun GlobalTypeSubstitutor(
    globalType: GlobalType,
    substitution: Substitution,
    valueTypeSubstitutor: TypeSubstitutor<ValueType>,
): GlobalType = globalType.apply {
    valueType = valueTypeSubstitutor(globalType.valueType, substitution)
}
