package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ir.type.ArrayType as IRArrayType
import io.github.charlietap.chasm.ir.type.FieldType as IRFieldType

internal fun ArrayTypeFactory(
    arrayType: ArrayType,
): IRArrayType = ArrayTypeFactory(
    arrayType = arrayType,
    fieldTypeFactory = ::FieldTypeFactory,
)

internal inline fun ArrayTypeFactory(
    arrayType: ArrayType,
    fieldTypeFactory: IRFactory<FieldType, IRFieldType>,
): IRArrayType {
    return IRArrayType(
        fieldType = fieldTypeFactory(arrayType.fieldType),
    )
}
