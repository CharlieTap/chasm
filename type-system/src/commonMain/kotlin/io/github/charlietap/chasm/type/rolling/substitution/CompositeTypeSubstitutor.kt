package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType

internal fun CompositeTypeSubstitutor(
    compositeType: CompositeType,
    substitution: Substitution,
): CompositeType =
    CompositeTypeSubstitutor(
        compositeType = compositeType,
        substitution = substitution,
        structTypeSubstitutor = ::StructTypeSubstitutor,
        arrayTypeSubstitutor = ::ArrayTypeSubstitutor,
        functionTypeSubstitutor = ::FunctionTypeSubstitutor,
    )

internal fun CompositeTypeSubstitutor(
    compositeType: CompositeType,
    substitution: Substitution,
    structTypeSubstitutor: TypeSubstitutor<StructType>,
    arrayTypeSubstitutor: TypeSubstitutor<ArrayType>,
    functionTypeSubstitutor: TypeSubstitutor<FunctionType>,
): CompositeType = when (compositeType) {
    is CompositeType.Struct -> CompositeType.Struct(
        structTypeSubstitutor(compositeType.structType, substitution),
    )
    is CompositeType.Array -> CompositeType.Array(
        arrayTypeSubstitutor(compositeType.arrayType, substitution),
    )
    is CompositeType.Function -> CompositeType.Function(
        functionTypeSubstitutor(compositeType.functionType, substitution),
    )
}
