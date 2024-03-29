package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType

fun arrayType(
    field: FieldType = fieldType(),
) = ArrayType(field)
