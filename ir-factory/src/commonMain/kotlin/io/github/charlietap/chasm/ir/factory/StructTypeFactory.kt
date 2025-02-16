package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.ir.type.FieldType as IRFieldType
import io.github.charlietap.chasm.ir.type.StructType as IRStructType

internal fun StructTypeFactory(
    structType: StructType,
): IRStructType = StructTypeFactory(
    structType = structType,
    fieldTypeFactory = ::FieldTypeFactory,
)

internal inline fun StructTypeFactory(
    structType: StructType,
    fieldTypeFactory: IRFactory<FieldType, IRFieldType>,
): IRStructType {
    return IRStructType(
        fields = structType.fields.map(fieldTypeFactory),
    )
}
