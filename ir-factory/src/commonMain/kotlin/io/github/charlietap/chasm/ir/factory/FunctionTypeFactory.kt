package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ir.type.FunctionType as IRFunctionType
import io.github.charlietap.chasm.ir.type.ResultType as IRResultType

internal fun FunctionTypeFactory(
    functionType: FunctionType,
): IRFunctionType = FunctionTypeFactory(
    functionType = functionType,
    resultTypeFactory = ::ResultTypeFactory,
)

internal inline fun FunctionTypeFactory(
    functionType: FunctionType,
    resultTypeFactory: IRFactory<ResultType, IRResultType>,
): IRFunctionType {
    return IRFunctionType(
        params = resultTypeFactory(functionType.params),
        results = resultTypeFactory(functionType.results),
    )
}
