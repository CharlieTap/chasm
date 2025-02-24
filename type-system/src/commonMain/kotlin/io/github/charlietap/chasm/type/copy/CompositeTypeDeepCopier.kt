package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType

fun CompositeTypeDeepCopier(
    input: CompositeType,
): CompositeType =
    CompositeTypeDeepCopier(
        input = input,
        arrayTypeCopier = ::ArrayTypeDeepCopier,
        functionTypeCopier = ::FunctionTypeDeepCopier,
        structTypeCopier = ::StructTypeDeepCopier,
    )

inline fun CompositeTypeDeepCopier(
    input: CompositeType,
    arrayTypeCopier: DeepCopier<ArrayType>,
    functionTypeCopier: DeepCopier<FunctionType>,
    structTypeCopier: DeepCopier<StructType>,
): CompositeType = when (input) {
    is CompositeType.Array -> CompositeType.Array(
        arrayTypeCopier(input.arrayType),
    )
    is CompositeType.Function -> CompositeType.Function(
        functionTypeCopier(input.functionType),
    )
    is CompositeType.Struct -> CompositeType.Struct(
        structTypeCopier(input.structType),
    )
}
