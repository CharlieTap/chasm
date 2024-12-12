package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType

fun compositeType() = functionCompositeType()

fun functionCompositeType(
    functionType: FunctionType = functionType(),
) = CompositeType.Function(functionType)

fun structCompositeType(
    structType: StructType = structType(),
) = CompositeType.Struct(structType)

fun arrayCompositeType(
    arrayType: ArrayType = arrayType(),
) = CompositeType.Array(arrayType)
