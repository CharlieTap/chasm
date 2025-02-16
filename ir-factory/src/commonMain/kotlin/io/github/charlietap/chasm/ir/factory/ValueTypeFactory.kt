package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.ir.type.BottomType as IRBottomType
import io.github.charlietap.chasm.ir.type.NumberType as IRNumberType
import io.github.charlietap.chasm.ir.type.ReferenceType as IRReferenceType
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType
import io.github.charlietap.chasm.ir.type.VectorType as IRVectorType

internal fun ValueTypeFactory(
    valueType: ValueType,
): IRValueType = ValueTypeFactory(
    valueType = valueType,
    numberTypeFactory = ::NumberTypeFactory,
    vectorTypeFactory = ::VectorTypeFactory,
    referenceTypeFactory = ::ReferenceTypeFactory,
)

internal inline fun ValueTypeFactory(
    valueType: ValueType,
    numberTypeFactory: IRFactory<NumberType, IRNumberType>,
    vectorTypeFactory: IRFactory<VectorType, IRVectorType>,
    referenceTypeFactory: IRFactory<ReferenceType, IRReferenceType>,
): IRValueType {
    return when (valueType) {
        is ValueType.Number -> IRValueType.Number(
            numberType = numberTypeFactory(valueType.numberType),
        )

        is ValueType.Vector -> IRValueType.Vector(
            vectorType = vectorTypeFactory(valueType.vectorType),
        )

        is ValueType.Reference -> IRValueType.Reference(
            referenceType = referenceTypeFactory(valueType.referenceType),
        )

        is ValueType.Bottom -> IRValueType.Bottom(
            bottomType = IRBottomType,
        )
    }
}
