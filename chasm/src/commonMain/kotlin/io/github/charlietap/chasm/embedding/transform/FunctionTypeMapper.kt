package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.ast.type.FunctionType as InternalFunctionType
import io.github.charlietap.chasm.ast.type.ValueType as InternalValueType

internal class FunctionTypeMapper(
    private val valueTypeMapper: BidirectionalMapper<ValueType, InternalValueType> = ValueTypeMapper.instance,
) : BidirectionalMapper<FunctionType, InternalFunctionType> {
    override fun map(input: FunctionType): InternalFunctionType {
        return InternalFunctionType(
            ResultType(input.params.map(valueTypeMapper::map)),
            ResultType(input.results.map(valueTypeMapper::map)),
        )
    }

    override fun bimap(input: InternalFunctionType): FunctionType {
        return FunctionType(
            input.params.types.map(valueTypeMapper::bimap),
            input.results.types.map(valueTypeMapper::bimap),
        )
    }

    companion object {
        val instance = FunctionTypeMapper()
    }
}
