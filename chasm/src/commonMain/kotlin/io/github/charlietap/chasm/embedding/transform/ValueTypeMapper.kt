package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.ast.type.ValueType as InternalValueType

internal class ValueTypeMapper(
    private val referenceTypeMapper: BidirectionalMapper<ValueType.Reference, ReferenceType> = ReferenceTypeMapper,
) : BidirectionalMapper<ValueType, InternalValueType> {

    override fun map(input: ValueType): InternalValueType {
        return when (input) {
            is ValueType.Number.I32 -> InternalValueType.Number(NumberType.I32)
            is ValueType.Number.I64 -> InternalValueType.Number(NumberType.I64)
            is ValueType.Number.F32 -> InternalValueType.Number(NumberType.F32)
            is ValueType.Number.F64 -> InternalValueType.Number(NumberType.F64)
            is ValueType.Reference.Ref -> InternalValueType.Reference(referenceTypeMapper.map(input))
            is ValueType.Reference.RefNull -> InternalValueType.Reference(referenceTypeMapper.map(input))
        }
    }

    override fun bimap(input: InternalValueType): ValueType {
        return when (input) {
            is InternalValueType.Number -> when (input.numberType) {
                NumberType.I32 -> ValueType.Number.I32
                NumberType.I64 -> ValueType.Number.I64
                NumberType.F32 -> ValueType.Number.F32
                NumberType.F64 -> ValueType.Number.F64
            }
            is InternalValueType.Reference -> when (input.referenceType) {
                is ReferenceType.Ref -> referenceTypeMapper.bimap(input.referenceType)
                is ReferenceType.RefNull -> referenceTypeMapper.bimap(input.referenceType)
            }
            is InternalValueType.Bottom,
            is InternalValueType.Vector,
            -> throw IllegalStateException()
        }
    }

    companion object {
        val instance = ValueTypeMapper()
    }
}
