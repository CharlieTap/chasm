package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.HostParameters
import io.github.charlietap.chasm.host.HostResults
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.host.RawHostFunction
import io.github.charlietap.chasm.runtime.host.RawHostModuleInstance
import io.github.charlietap.chasm.runtime.host.RawHostResources
import io.github.charlietap.chasm.type.FunctionType

internal interface HostCallAdapters {

    fun adapt(
        type: FunctionType,
        function: HostFunction,
    ): RawHostFunction
}

internal class InterpreterHostCallAdapters(
    private val codec: ReferenceCodec,
    private val lanes: ReferenceLanes,
    private val resources: InterpreterHostResources,
    private val callConfig: HostCallConfig,
) : HostCallAdapters {

    override fun adapt(
        type: FunctionType,
        function: HostFunction,
    ): RawHostFunction {

        val parameterSlots = codec.slots(type.params.types)
        val resultSlots = codec.slots(type.results.types)

        return if (parameterSlots.isEmpty && resultSlots.isEmpty) {
            NumericHostCall(function, resources, callConfig)
        } else {
            ReferenceHostCall(function, parameterSlots, resultSlots, codec, lanes, resources, callConfig)
        }
    }
}

internal class NumericHostCall(
    private val function: HostFunction,
    private val resources: InterpreterHostResources,
    private val callConfig: HostCallConfig,
) : RawHostFunction {

    context(stack: HostStack, module: RawHostModuleInstance, raw: RawHostResources)
    override fun invoke(
        parameters: HostParameters,
        results: HostResults,
    ) {

        val context = raw as ExecutionContext
        val previousConfig = callConfig.current
        callConfig.current = context.config

        try {
            context(stack, NO_REFERENCES, resources.module(module, context), resources) {
                function.invoke(parameters, results)
            }
        } finally {
            callConfig.current = previousConfig
        }
    }

    private companion object {
        val NO_REFERENCES = arrayOfNulls<Any>(0)
    }
}

internal class ReferenceHostCall(
    private val function: HostFunction,
    private val parameterSlots: ReferenceSlots,
    private val resultSlots: ReferenceSlots,
    private val codec: ReferenceCodec,
    private val lanes: ReferenceLanes,
    private val resources: InterpreterHostResources,
    private val callConfig: HostCallConfig,
) : RawHostFunction {

    context(stack: HostStack, module: RawHostModuleInstance, raw: RawHostResources)
    override fun invoke(
        parameters: HostParameters,
        results: HostResults,
    ) {

        val context = raw as ExecutionContext
        val parametersEnd = parameters + parameterSlots.valueCount
        val resultsEnd = results + resultSlots.valueCount
        val lane = lanes.acquire(maxOf(parametersEnd, resultsEnd))
        val previousConfig = callConfig.current
        callConfig.current = context.config

        try {
            decodeParameters(stack, lane, parameters)

            context(stack, lane, resources.module(module, context), resources) {
                function.invoke(parameters, results)
            }

            encodeResults(stack, lane, results)
        } finally {
            lane.fill(null, parameters, parametersEnd)
            lane.fill(null, results, resultsEnd)
            callConfig.current = previousConfig
            lanes.release()
        }
    }

    private fun decodeParameters(
        stack: HostStack,
        lane: Array<Any?>,
        parameters: HostParameters,
    ) {
        for (index in parameterSlots.valueIndices) {
            val slot = parameters + index
            lane[slot] = codec.decode(stack[slot])
        }
    }

    private fun encodeResults(
        stack: HostStack,
        lane: Array<Any?>,
        results: HostResults,
    ) {

        val valueIndices = resultSlots.valueIndices
        val types = resultSlots.types

        for (position in valueIndices.indices) {
            val index = valueIndices[position]
            val slot = results + index
            stack[slot] = encodeResult(types[position], lane[slot], index)
        }
    }

    private fun encodeResult(
        type: ResolvedReferenceType,
        value: Any?,
        index: Int,
    ): Long {
        return try {
            codec.encode(type, value)
        } catch (exception: IllegalArgumentException) {
            throw HostFunctionException("result $index: ${exception.message}")
        }
    }
}

internal class ReferenceLanes {

    private var lanes = arrayOfNulls<Array<Any?>>(INITIAL_DEPTH)
    private var depth = 0

    fun acquire(size: Int): Array<Any?> {

        if (depth == lanes.size) {
            lanes = lanes.copyOf(depth * 2)
        }

        val current = lanes[depth]
        val lane = if (current != null && current.size >= size) {
            current
        } else {
            arrayOfNulls<Any>(maxOf(size, MINIMUM_SIZE, (current?.size ?: 0) * 2)).also { created -> lanes[depth] = created }
        }
        depth++

        return lane
    }

    fun release() {
        depth--
    }

    private companion object {
        const val INITIAL_DEPTH = 4
        const val MINIMUM_SIZE = 16
    }
}
