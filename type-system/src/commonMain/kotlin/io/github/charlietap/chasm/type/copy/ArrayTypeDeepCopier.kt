package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType

internal fun ArrayTypeDeepCopier(
    input: ArrayType,
): ArrayType =
    ArrayTypeDeepCopier(
        input = input,
        fieldTypeCopier = ::FieldTypeDeepCopier,
    )

internal inline fun ArrayTypeDeepCopier(
    input: ArrayType,
    fieldTypeCopier: DeepCopier<FieldType>,
): ArrayType = ArrayType(
    fieldTypeCopier(fieldTypeCopier(input.fieldType)),
)
