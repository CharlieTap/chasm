package io.github.charlietap.chasm.compiler.frontend.ext

import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.cir.AbstractHeapType as CirAbstractHeapType
import io.github.charlietap.chasm.cir.HeapType as CirHeapType
import io.github.charlietap.chasm.cir.PackedType as CirPackedType
import io.github.charlietap.chasm.cir.ReferenceType as CirReferenceType
import io.github.charlietap.chasm.cir.ValueType as CirValueType

internal fun ReferenceType.toCir(): CirReferenceType = CirReferenceType(
    nullable = this is ReferenceType.RefNull,
    heapType = heapType.toCir(),
)

internal fun HeapType.toCir(): CirHeapType = when (this) {
    AbstractHeapType.Func -> CirHeapType.Abstract(CirAbstractHeapType.Func)
    AbstractHeapType.NoFunc -> CirHeapType.Abstract(CirAbstractHeapType.NoFunc)
    AbstractHeapType.Extern -> CirHeapType.Abstract(CirAbstractHeapType.Extern)
    AbstractHeapType.NoExtern -> CirHeapType.Abstract(CirAbstractHeapType.NoExtern)
    AbstractHeapType.Exception -> CirHeapType.Abstract(CirAbstractHeapType.Exception)
    AbstractHeapType.NoException -> CirHeapType.Abstract(CirAbstractHeapType.NoException)
    AbstractHeapType.Any -> CirHeapType.Abstract(CirAbstractHeapType.Any)
    AbstractHeapType.Eq -> CirHeapType.Abstract(CirAbstractHeapType.Eq)
    AbstractHeapType.Struct -> CirHeapType.Abstract(CirAbstractHeapType.Struct)
    AbstractHeapType.Array -> CirHeapType.Abstract(CirAbstractHeapType.Array)
    AbstractHeapType.I31 -> CirHeapType.Abstract(CirAbstractHeapType.I31)
    AbstractHeapType.None -> CirHeapType.Abstract(CirAbstractHeapType.None)
    is AbstractHeapType.Bottom -> error("bottom heap types do not appear in compiled code")
    is ConcreteHeapType.TypeIndex -> CirHeapType.Concrete(TypeIndex(index))
    is ConcreteHeapType.Defined -> CirHeapType.Concrete(TypeIndex(definedType.typeIndex))
    is ConcreteHeapType.RecursiveTypeIndex -> error("instruction type is not closed")
}

internal fun PackedType.toCir(): CirPackedType = when (this) {
    PackedType.I8 -> CirPackedType.I8
    PackedType.I16 -> CirPackedType.I16
}

internal fun ValueType.toCir(): CirValueType = when (this) {
    is ValueType.Number -> when (numberType) {
        NumberType.I32 -> CirValueType.I32
        NumberType.I64 -> CirValueType.I64
        NumberType.F32 -> CirValueType.F32
        NumberType.F64 -> CirValueType.F64
    }
    is ValueType.Vector -> CirValueType.V128
    is ValueType.Reference -> CirValueType.Reference(referenceType.toCir())
    is ValueType.Bottom -> error("bottom types do not appear in compiled code")
}
