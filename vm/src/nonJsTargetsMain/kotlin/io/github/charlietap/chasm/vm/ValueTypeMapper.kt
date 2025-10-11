package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.type.AbstractHeapType as ChasmAbstractHeapType
import io.github.charlietap.chasm.type.BottomType as ChasmBottomType
import io.github.charlietap.chasm.type.HeapType as ChasmHeapType
import io.github.charlietap.chasm.type.NumberType as ChasmNumberType
import io.github.charlietap.chasm.type.ReferenceType as ChasmReferenceType
import io.github.charlietap.chasm.type.ValueType as ChasmValueType
import io.github.charlietap.chasm.type.VectorType as ChasmVectorType

internal object NumberTypeMapper {

    fun from(numberType: NumberType): ChasmNumberType = when (numberType) {
        NumberType.I32 -> ChasmNumberType.I32
        NumberType.I64 -> ChasmNumberType.I64
        NumberType.F32 -> ChasmNumberType.F32
        NumberType.F64 -> ChasmNumberType.F64
    }

    fun to(numberType: ChasmNumberType): NumberType = when (numberType) {
        ChasmNumberType.I32 -> NumberType.I32
        ChasmNumberType.I64 -> NumberType.I64
        ChasmNumberType.F32 -> NumberType.F32
        ChasmNumberType.F64 -> NumberType.F64
    }
}

internal object ReferenceTypeMapper {

    fun from(referenceType: ReferenceType): ChasmReferenceType = when (referenceType) {
        is ReferenceType.Ref -> ChasmReferenceType.Ref(from(referenceType.heapType))
        is ReferenceType.RefNull -> ChasmReferenceType.RefNull(from(referenceType.heapType))
    }

    fun to(referenceType: ChasmReferenceType): ReferenceType = when (referenceType) {
        is ChasmReferenceType.Ref -> ReferenceType.Ref(to(referenceType.heapType))
        is ChasmReferenceType.RefNull -> ReferenceType.RefNull(to(referenceType.heapType))
    }

    private fun from(heapType: HeapType): ChasmHeapType = when (heapType) {
        is AbstractHeapType -> from(heapType)
    }

    private fun to(heapType: ChasmHeapType): HeapType = when (heapType) {
        is ChasmAbstractHeapType -> to(heapType)
        else -> throw IllegalArgumentException("Concrete heap types are not supported in vm HeapType: $heapType")
    }

    private fun from(heapType: AbstractHeapType): ChasmAbstractHeapType = when (heapType) {
        AbstractHeapType.Func -> ChasmAbstractHeapType.Func
        AbstractHeapType.NoFunc -> ChasmAbstractHeapType.NoFunc
        AbstractHeapType.Extern -> ChasmAbstractHeapType.Extern
        AbstractHeapType.NoExtern -> ChasmAbstractHeapType.NoExtern
        AbstractHeapType.Exception -> ChasmAbstractHeapType.Exception
        AbstractHeapType.NoException -> ChasmAbstractHeapType.NoException
        AbstractHeapType.Any -> ChasmAbstractHeapType.Any
        AbstractHeapType.Eq -> ChasmAbstractHeapType.Eq
        AbstractHeapType.Struct -> ChasmAbstractHeapType.Struct
        AbstractHeapType.Array -> ChasmAbstractHeapType.Array
        AbstractHeapType.I31 -> ChasmAbstractHeapType.I31
        AbstractHeapType.None -> ChasmAbstractHeapType.None
        AbstractHeapType.Bottom -> ChasmAbstractHeapType.Bottom(ChasmBottomType)
    }

    private fun to(heapType: ChasmAbstractHeapType): AbstractHeapType = when (heapType) {
        ChasmAbstractHeapType.Func -> AbstractHeapType.Func
        ChasmAbstractHeapType.NoFunc -> AbstractHeapType.NoFunc
        ChasmAbstractHeapType.Extern -> AbstractHeapType.Extern
        ChasmAbstractHeapType.NoExtern -> AbstractHeapType.NoExtern
        ChasmAbstractHeapType.Exception -> AbstractHeapType.Exception
        ChasmAbstractHeapType.NoException -> AbstractHeapType.NoException
        ChasmAbstractHeapType.Any -> AbstractHeapType.Any
        ChasmAbstractHeapType.Eq -> AbstractHeapType.Eq
        ChasmAbstractHeapType.Struct -> AbstractHeapType.Struct
        ChasmAbstractHeapType.Array -> AbstractHeapType.Array
        ChasmAbstractHeapType.I31 -> AbstractHeapType.I31
        ChasmAbstractHeapType.None -> AbstractHeapType.None
        is ChasmAbstractHeapType.Bottom -> AbstractHeapType.Bottom
    }
}

internal object ValueTypeMapper {

    fun from(valueType: ValueType): ChasmValueType = when (valueType) {
        is ValueType.Number -> ChasmValueType.Number(NumberTypeMapper.from(valueType.numberType))
        is ValueType.Vector -> ChasmValueType.Vector(
            when (valueType.vectorType) {
                VectorType.V128 -> ChasmVectorType.V128
            },
        )
        is ValueType.Reference -> ChasmValueType.Reference(ReferenceTypeMapper.from(valueType.referenceType))
    }

    fun to(valueType: ChasmValueType): ValueType = when (valueType) {
        is ChasmValueType.Number -> ValueType.Number(NumberTypeMapper.to(valueType.numberType))
        is ChasmValueType.Vector -> ValueType.Vector(
            when (valueType.vectorType) {
                ChasmVectorType.V128 -> VectorType.V128
            },
        )
        is ChasmValueType.Reference -> ValueType.Reference(ReferenceTypeMapper.to(valueType.referenceType))
        is ChasmValueType.Bottom -> throw IllegalArgumentException("Cannot map bottom value type to vm ValueType: $valueType")
    }
}
