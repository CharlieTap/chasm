package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction as InternalHostFunction

internal class HostFunctionMapper(
    private val valueMapper: BidirectionalMapper<Value, ExecutionValue> = ValueMapper.instance,
) : BidirectionalMapper<HostFunction, InternalHostFunction> {

    override fun map(input: HostFunction): InternalHostFunction {
        return { params: List<ExecutionValue> ->
            val mappedParams = params.map(valueMapper::bimap)
            val results = input(mappedParams)
            results.map(valueMapper::map)
        }
    }

    override fun bimap(input: InternalHostFunction): HostFunction {
        return object : HostFunction {
            override fun invoke(params: List<Value>): List<Value> {
                val mappedParams = params.map(valueMapper::map)
                val results = input.invoke(mappedParams)
                return results.map(valueMapper::bimap)
            }
        }
    }

    companion object {
        val instance = HostFunctionMapper()
    }
}
