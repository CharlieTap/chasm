package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType

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
