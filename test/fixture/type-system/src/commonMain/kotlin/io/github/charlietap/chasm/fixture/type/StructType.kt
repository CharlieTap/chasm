package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType

fun structType(
    fields: List<FieldType> = emptyList(),
) = StructType(fields)
