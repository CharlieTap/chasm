package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ir.type.ResultType as IRResultType
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType

internal fun ResultTypeFactory(
    resultType: ResultType,
): IRResultType = ResultTypeFactory(
    resultType = resultType,
    valueTypeFactory = ::ValueTypeFactory,
)

internal inline fun ResultTypeFactory(
    resultType: ResultType,
    valueTypeFactory: IRFactory<ValueType, IRValueType>,
): IRResultType {
    return IRResultType(
        types = resultType.types.map(valueTypeFactory),
    )
}
