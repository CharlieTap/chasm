package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.type.FunctionType as ChasmFunctionType
import io.github.charlietap.chasm.type.ResultType as ChasmResultType

internal object FunctionTypeMapper {

    fun from(functionType: FunctionType): ChasmFunctionType {
        val params = functionType.params.map(ValueTypeMapper::from)
        val results = functionType.results.map(ValueTypeMapper::from)
        return ChasmFunctionType(
            params = ChasmResultType(params),
            results = ChasmResultType(results),
        )
    }

    fun to(functionType: ChasmFunctionType): FunctionType {
        val params = functionType.params.types.map(ValueTypeMapper::to)
        val results = functionType.results.types.map(ValueTypeMapper::to)
        return FunctionType(
            params = params,
            results = results,
        )
    }
}
