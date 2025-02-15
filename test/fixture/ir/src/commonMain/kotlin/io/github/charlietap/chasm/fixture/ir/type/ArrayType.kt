package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.ArrayType
import io.github.charlietap.chasm.ir.type.FieldType

fun arrayType(
    fieldType: FieldType = fieldType(),
) = ArrayType(fieldType)
