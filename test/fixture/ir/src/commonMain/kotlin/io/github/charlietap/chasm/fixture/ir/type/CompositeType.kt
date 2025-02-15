package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.ArrayType
import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.StructType

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
