package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.ResultType
import io.github.charlietap.chasm.ir.type.ValueType

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
        ResultType(params),
        ResultType(results),
    )
}
