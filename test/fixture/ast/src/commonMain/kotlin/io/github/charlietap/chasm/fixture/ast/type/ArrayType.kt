package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType

fun arrayType(
    fieldType: FieldType = fieldType(),
) = ArrayType(fieldType)
