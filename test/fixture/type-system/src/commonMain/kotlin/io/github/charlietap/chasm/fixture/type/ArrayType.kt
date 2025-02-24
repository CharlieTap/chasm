package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType

fun arrayType(
    fieldType: FieldType = fieldType(),
) = ArrayType(fieldType)
