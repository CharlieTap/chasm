package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType

internal fun ArrayTypeSubstitutor(
    arrayType: ArrayType,
    substitution: Substitution,
): ArrayType =
    ArrayTypeSubstitutor(
        arrayType = arrayType,
        substitution = substitution,
        fieldTypeSubstitutor = ::FieldTypeSubstitutor,
    )

internal fun ArrayTypeSubstitutor(
    arrayType: ArrayType,
    substitution: Substitution,
    fieldTypeSubstitutor: TypeSubstitutor<FieldType>,
): ArrayType = ArrayType(
    fieldTypeSubstitutor(arrayType.fieldType, substitution),
)
