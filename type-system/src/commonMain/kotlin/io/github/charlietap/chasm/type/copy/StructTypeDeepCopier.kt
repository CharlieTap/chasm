package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType

fun StructTypeDeepCopier(
    input: StructType,
): StructType =
    StructTypeDeepCopier(
        input = input,
        fieldTypeCopier = ::FieldTypeDeepCopier,
    )

inline fun StructTypeDeepCopier(
    input: StructType,
    fieldTypeCopier: DeepCopier<FieldType>,
): StructType = StructType(
    fields = input.fields.map(fieldTypeCopier),
)
