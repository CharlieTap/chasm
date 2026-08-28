package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

fun valueTypeList(builder: ValueTypeListBuilder.() -> Unit): List<ValueType> {
    return ValueTypeListBuilder().apply(builder).build()
}

class ValueTypeListBuilder {

    private val values = mutableListOf<ValueType>()

    fun i32() = add(ValueType.Number(NumberType.I32))

    fun i64() = add(ValueType.Number(NumberType.I64))

    fun f32() = add(ValueType.Number(NumberType.F32))

    fun f64() = add(ValueType.Number(NumberType.F64))

    fun reference(referenceType: ReferenceType) = add(ValueType.Reference(referenceType))

    fun ref(heapType: HeapType) = reference(ReferenceType.Ref(heapType))

    fun refNull(heapType: HeapType) = reference(ReferenceType.RefNull(heapType))

    fun funcref() = refNull(AbstractHeapType.Func)

    fun exnref() = refNull(AbstractHeapType.Exception)

    fun externref() = refNull(AbstractHeapType.Extern)

    fun anyref() = refNull(AbstractHeapType.Any)

    fun eqref() = refNull(AbstractHeapType.Eq)

    fun i31ref() = refNull(AbstractHeapType.I31)

    fun structref() = refNull(AbstractHeapType.Struct)

    fun arrayref() = refNull(AbstractHeapType.Array)

    fun build(): List<ValueType> = values

    private fun add(valueType: ValueType) {
        values.add(valueType)
    }
}
