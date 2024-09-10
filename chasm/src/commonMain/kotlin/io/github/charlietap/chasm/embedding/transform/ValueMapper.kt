package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

internal class ValueMapper(
    private val referenceMapper: BidirectionalMapper<Value.Reference, ReferenceValue> = ReferenceValueMapper.instance,
) : BidirectionalMapper<Value, ExecutionValue> {
    override fun map(input: Value): ExecutionValue {
        return when (input) {
            is Value.Number.I32 -> NumberValue.I32(input.value)
            is Value.Number.I64 -> NumberValue.I64(input.value)
            is Value.Number.F32 -> NumberValue.F32(input.value)
            is Value.Number.F64 -> NumberValue.F64(input.value)
            is Value.Reference.Array -> referenceMapper.map(input)
            is Value.Reference.Exception -> referenceMapper.map(input)
            is Value.Reference.Extern -> referenceMapper.map(input)
            is Value.Reference.Func -> referenceMapper.map(input)
            is Value.Reference.Host -> referenceMapper.map(input)
            is Value.Reference.I31 -> referenceMapper.map(input)
            is Value.Reference.Null -> referenceMapper.map(input)
            is Value.Reference.Struct -> referenceMapper.map(input)
        }
    }

    override fun bimap(input: ExecutionValue): Value {
        return when (input) {
            is NumberValue.I32 -> Value.Number.I32(input.value)
            is NumberValue.I64 -> Value.Number.I64(input.value)
            is NumberValue.F32 -> Value.Number.F32(input.value)
            is NumberValue.F64 -> Value.Number.F64(input.value)
            is ReferenceValue.Array -> referenceMapper.bimap(input)
            is ReferenceValue.Exception -> referenceMapper.bimap(input)
            is ReferenceValue.Extern -> referenceMapper.bimap(input)
            is ReferenceValue.Function -> referenceMapper.bimap(input)
            is ReferenceValue.Host -> referenceMapper.bimap(input)
            is ReferenceValue.I31 -> referenceMapper.bimap(input)
            is ReferenceValue.Null -> referenceMapper.bimap(input)
            is ReferenceValue.Struct -> referenceMapper.bimap(input)
            is VectorValue.V128, ExecutionValue.Uninitialised -> throw IllegalStateException(
                "Cannot map internal runtime values to public api",
            )
        }
    }

    companion object {
        val instance = ValueMapper()
    }
}
