package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType

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
