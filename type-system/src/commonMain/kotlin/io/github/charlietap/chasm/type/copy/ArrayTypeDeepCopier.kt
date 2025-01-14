package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType

fun ArrayTypeDeepCopier(
    input: ArrayType,
): ArrayType =
    ArrayTypeDeepCopier(
        input = input,
        fieldTypeCopier = ::FieldTypeDeepCopier,
    )

inline fun ArrayTypeDeepCopier(
    input: ArrayType,
    fieldTypeCopier: DeepCopier<FieldType>,
): ArrayType = ArrayType(
    fieldTypeCopier(fieldTypeCopier(input.fieldType)),
)
