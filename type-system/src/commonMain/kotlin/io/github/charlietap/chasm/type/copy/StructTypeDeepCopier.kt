package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType

internal fun StructTypeDeepCopier(
    input: StructType,
): StructType =
    StructTypeDeepCopier(
        input = input,
        fieldTypeCopier = ::FieldTypeDeepCopier,
    )

internal inline fun StructTypeDeepCopier(
    input: StructType,
    fieldTypeCopier: DeepCopier<FieldType>,
): StructType = StructType(
    fields = input.fields.map(fieldTypeCopier),
)
