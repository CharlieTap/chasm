package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.ValueType

fun valueTypeList(builder: ValueTypeListBuilder.() -> Unit): List<ValueType> {
    return ValueTypeListBuilder().apply(builder).build()
}

class ValueTypeListBuilder {

    private val values = mutableListOf<ValueType>()

    fun i32() = values.add(ValueType.Number.I32)

    fun i64() = values.add(ValueType.Number.I64)

    fun f32() = values.add(ValueType.Number.F32)

    fun f64() = values.add(ValueType.Number.F64)

    fun build(): List<ValueType> = values
}
