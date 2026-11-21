package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostParameters
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostResults
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

abstract class ObjectLaneHostFunction(
    private val type: FunctionType,
) : HostFunction {

    private val parameterTypes = type.params.types
    private val resultTypes = type.results.types
    private val carriesReferences = (parameterTypes + resultTypes).any { value -> value is ValueType.Reference }

    private val resultReferenceTypes = resultTypes.mapIndexedNotNull { index, result -> (result as? ValueType.Reference)?.let { reference -> index to reference } }

    @Volatile
    private var resultChecks: ResultChecks? = null

    fun checkResults(
        checks: ReferenceChecks,
        references: Array<Any?>,
    ) {

        if (resultReferenceTypes.isEmpty()) {
            return
        }

        val compiled = resultChecks(checks)
        for (position in compiled.indices.indices) {
            val index = compiled.indices[position]
            if (!compiled.checks[position].accepts(references[index])) {
                throw InvocationException(InvocationError.HostFunctionInconsistentWithType(resultTypes[index], null))
            }
        }
    }

    abstract fun call(
        stack: LongArray,
        references: Array<Any?>,
        parameters: Int,
        results: Int,
        caller: HostModuleInstance,
        resources: HostResources,
    )

    context(stack: HostStack, caller: HostModuleInstance, resources: HostResources)
    override fun invoke(
        parameters: HostParameters,
        results: HostResults,
    ) {

        if (!carriesReferences) {
            return call(stack, NO_REFERENCES, parameters, results, caller, resources)
        }

        val bridge = checkNotNull((resources as ExecutionContext).store.referenceBridge) { "reference host functions need a JIT store" }
        val slots = maxOf(parameterTypes.size, resultTypes.size)
        val words = LongArray(slots)
        val objects = arrayOfNulls<Any>(slots)
        parameterTypes.forEachIndexed { index, parameter ->
            if (parameter is ValueType.Reference) {
                objects[index] = bridge.fromHost(stack[parameters + index])
            } else {
                words[index] = stack[parameters + index]
            }
        }

        call(words, objects, 0, 0, caller, resources)

        resultTypes.forEachIndexed { index, result ->
            stack[results + index] = if (result is ValueType.Reference) {
                bridge.toHostResult(objects[index], result.referenceType.heapType.let { heap -> heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern })
            } else {
                words[index]
            }
        }
    }

    private fun resultChecks(checks: ReferenceChecks): ResultChecks {

        val cached = resultChecks
        if (cached != null && cached.owner === checks) {
            return cached
        }

        val compiled = ResultChecks(
            owner = checks,
            indices = IntArray(resultReferenceTypes.size) { position -> resultReferenceTypes[position].first },
            checks = Array(resultReferenceTypes.size) { position -> checks.check(resultReferenceTypes[position].second.referenceType, null) },
        )
        resultChecks = compiled

        return compiled
    }

    private class ResultChecks(
        val owner: ReferenceChecks,
        val indices: IntArray,
        val checks: Array<ReferenceCheck>,
    )

    private companion object {
        val NO_REFERENCES = arrayOfNulls<Any>(0)
    }
}
