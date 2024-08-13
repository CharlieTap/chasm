package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.ValueType

class FunctionTypeBuilder {

    private var params = listOf<ValueType>()
    private var results = listOf<ValueType>()

    fun params(builder: ValueTypeListBuilder.() -> Unit) {
        params = ValueTypeListBuilder().apply(builder).build()
    }

    fun results(builder: ValueTypeListBuilder.() -> Unit) {
        results = ValueTypeListBuilder().apply(builder).build()
    }

    fun build() = FunctionType(
        params,
        results,
    )
}
