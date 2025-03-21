package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType

internal fun StructTypeSubstitutor(
    structType: StructType,
    substitution: Substitution,
): StructType =
    StructTypeSubstitutor(
        structType = structType,
        substitution = substitution,
        fieldTypeSubstitutor = ::FieldTypeSubstitutor,
    )

internal fun StructTypeSubstitutor(
    structType: StructType,
    substitution: Substitution,
    fieldTypeSubstitutor: TypeSubstitutor<FieldType>,
): StructType = StructType(
    structType.fields.map { fieldType ->
        fieldTypeSubstitutor(fieldType, substitution)
    },
)
