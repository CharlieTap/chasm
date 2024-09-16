package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType

internal fun StructTypeSubstitutorImpl(
    structType: StructType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): StructType =
    StructTypeSubstitutorImpl(
        structType = structType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        fieldTypeSubstitutor = ::FieldTypeSubstitutor,
    )

internal fun StructTypeSubstitutorImpl(
    structType: StructType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    fieldTypeSubstitutor: TypeSubstitutor<FieldType>,
): StructType = StructType(
    structType.fields.map { fieldType ->
        fieldTypeSubstitutor(fieldType, concreteHeapTypeSubstitutor)
    },
)
