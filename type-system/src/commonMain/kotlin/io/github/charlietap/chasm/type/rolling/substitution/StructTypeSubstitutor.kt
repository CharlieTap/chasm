package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType

internal fun StructTypeSubstitutor(
    structType: StructType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): StructType =
    StructTypeSubstitutor(
        structType = structType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        fieldTypeSubstitutor = ::FieldTypeSubstitutor,
    )

internal fun StructTypeSubstitutor(
    structType: StructType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    fieldTypeSubstitutor: TypeSubstitutor<FieldType>,
): StructType = StructType(
    structType.fields.map { fieldType ->
        fieldTypeSubstitutor(fieldType, concreteHeapTypeSubstitutor)
    },
)
