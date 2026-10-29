package io.github.charlietap.chasm.compiler.backend.analysis.ext

import io.github.charlietap.chasm.cir.AbstractHeapType
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.cir.ValueType
import io.github.charlietap.chasm.runtime.ext.defaultBits
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.VectorType
import io.github.charlietap.chasm.type.AbstractHeapType as TypeAbstractHeapType
import io.github.charlietap.chasm.type.HeapType as TypeHeapType
import io.github.charlietap.chasm.type.ReferenceType as TypeReferenceType
import io.github.charlietap.chasm.type.ValueType as TypeValueType

fun ValueType.toType(): TypeValueType = when (this) {
    ValueType.I32 -> TypeValueType.Number(NumberType.I32)
    ValueType.I64 -> TypeValueType.Number(NumberType.I64)
    ValueType.F32 -> TypeValueType.Number(NumberType.F32)
    ValueType.F64 -> TypeValueType.Number(NumberType.F64)
    ValueType.V128 -> TypeValueType.Vector(VectorType.V128)
    is ValueType.Reference -> TypeValueType.Reference(type.toType())
}

fun ReferenceType.toType(): TypeReferenceType = if (nullable) {
    TypeReferenceType.RefNull(heapType.toType())
} else {
    TypeReferenceType.Ref(heapType.toType())
}

fun HeapType.toType(): TypeHeapType = when (this) {
    is HeapType.Concrete -> ConcreteHeapType.TypeIndex(type.index)
    is HeapType.Abstract -> when (type) {
        AbstractHeapType.Func -> TypeAbstractHeapType.Func
        AbstractHeapType.NoFunc -> TypeAbstractHeapType.NoFunc
        AbstractHeapType.Extern -> TypeAbstractHeapType.Extern
        AbstractHeapType.NoExtern -> TypeAbstractHeapType.NoExtern
        AbstractHeapType.Exception -> TypeAbstractHeapType.Exception
        AbstractHeapType.NoException -> TypeAbstractHeapType.NoException
        AbstractHeapType.Any -> TypeAbstractHeapType.Any
        AbstractHeapType.Eq -> TypeAbstractHeapType.Eq
        AbstractHeapType.Struct -> TypeAbstractHeapType.Struct
        AbstractHeapType.Array -> TypeAbstractHeapType.Array
        AbstractHeapType.I31 -> TypeAbstractHeapType.I31
        AbstractHeapType.None -> TypeAbstractHeapType.None
    }
}

fun HeapType.nullReference(): Long = ReferenceValue.Null(toType()).toLong()

fun Frame.localInitialValue(
    index: Int,
): Long {
    if (index >= initialisedLocalCount) return 0L
    return when (val type = locals[index]) {
        is ValueType.Reference -> TypeValueType.Reference(type.type.toType()).defaultBits()!!
        else -> 0L
    }
}
