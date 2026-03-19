package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType

fun TableTypeSubstitutor(
    tableType: TableType,
    substitution: Substitution,
): TableType =
    TableTypeSubstitutor(
        tableType = tableType,
        substitution = substitution,
        referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
    )

internal fun TableTypeSubstitutor(
    tableType: TableType,
    substitution: Substitution,
    referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
): TableType = tableType.apply {
    referenceType = referenceTypeSubstitutor(tableType.referenceType, substitution)
}
