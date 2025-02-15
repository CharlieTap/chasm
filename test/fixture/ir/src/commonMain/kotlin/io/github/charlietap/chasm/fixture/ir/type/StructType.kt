package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.FieldType
import io.github.charlietap.chasm.ir.type.StructType

fun structType(
    fields: List<FieldType> = emptyList(),
) = StructType(fields)
