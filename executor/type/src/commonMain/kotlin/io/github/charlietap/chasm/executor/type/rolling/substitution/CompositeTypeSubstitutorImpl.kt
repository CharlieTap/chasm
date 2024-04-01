package io.github.charlietap.chasm.executor.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType

internal fun CompositeTypeSubstitutorImpl(
    compositeType: CompositeType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): CompositeType =
    CompositeTypeSubstitutorImpl(
        compositeType = compositeType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        structTypeSubstitutor = ::StructTypeSubstitutorImpl,
        arrayTypeSubstitutor = ::ArrayTypeSubstitutorImpl,
        functionTypeSubstitutor = ::FunctionTypeSubstitutorImpl,
    )

internal fun CompositeTypeSubstitutorImpl(
    compositeType: CompositeType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    structTypeSubstitutor: TypeSubstitutor<StructType>,
    arrayTypeSubstitutor: TypeSubstitutor<ArrayType>,
    functionTypeSubstitutor: TypeSubstitutor<FunctionType>,
): CompositeType = when (compositeType) {
    is CompositeType.Struct -> CompositeType.Struct(
        structTypeSubstitutor(compositeType.structType, concreteHeapTypeSubstitutor),
    )
    is CompositeType.Array -> CompositeType.Array(
        arrayTypeSubstitutor(compositeType.arrayType, concreteHeapTypeSubstitutor),
    )
    is CompositeType.Function -> CompositeType.Function(
        functionTypeSubstitutor(compositeType.functionType, concreteHeapTypeSubstitutor),
    )
}
