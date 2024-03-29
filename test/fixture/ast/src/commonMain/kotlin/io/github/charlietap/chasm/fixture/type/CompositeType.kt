package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.FunctionType

fun compositeType() = functionCompositeType()

fun functionCompositeType(
    functionType: FunctionType = functionType(),
) = CompositeType.Function(functionType)

fun structCompositeType(
    fields: List<FieldType> = emptyList(),
) = CompositeType.Struct(fields)

fun arrayCompositeType(
    field: FieldType = fieldType(),
) = CompositeType.Array(field)
