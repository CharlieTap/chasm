package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType

internal fun ArrayTypeSubstitutor(
    arrayType: ArrayType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ArrayType =
    ArrayTypeSubstitutor(
        arrayType = arrayType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        fieldTypeSubstitutor = ::FieldTypeSubstitutor,
    )

internal fun ArrayTypeSubstitutor(
    arrayType: ArrayType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    fieldTypeSubstitutor: TypeSubstitutor<FieldType>,
): ArrayType = ArrayType(
    fieldTypeSubstitutor(arrayType.fieldType, concreteHeapTypeSubstitutor),
)
