package io.github.charlietap.chasm.type.rolling.substitution

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType

internal fun ArrayTypeSubstitutorImpl(
    arrayType: ArrayType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
): ArrayType =
    ArrayTypeSubstitutorImpl(
        arrayType = arrayType,
        concreteHeapTypeSubstitutor = concreteHeapTypeSubstitutor,
        fieldTypeSubstitutor = ::FieldTypeSubstitutorImpl,
    )

internal fun ArrayTypeSubstitutorImpl(
    arrayType: ArrayType,
    concreteHeapTypeSubstitutor: ConcreteHeapTypeSubstitutor,
    fieldTypeSubstitutor: TypeSubstitutor<FieldType>,
): ArrayType = ArrayType(
    fieldTypeSubstitutor(arrayType.fieldType, concreteHeapTypeSubstitutor),
)
