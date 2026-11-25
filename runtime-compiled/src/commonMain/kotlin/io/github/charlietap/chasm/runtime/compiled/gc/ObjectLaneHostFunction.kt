package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.HostParameters
import io.github.charlietap.chasm.host.HostResults
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.host.RawHostFunction
import io.github.charlietap.chasm.runtime.host.RawHostModuleInstance
import io.github.charlietap.chasm.runtime.host.RawHostResources
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
abstract class ObjectLaneHostFunction(
    type: FunctionType,
) : RawHostFunction {

    private val resultTypes = type.results.types

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
        caller: RawHostModuleInstance,
        resources: RawHostResources,
    )

    context(stack: HostStack, caller: RawHostModuleInstance, resources: RawHostResources)
    override fun invoke(
        parameters: HostParameters,
        results: HostResults,
    ) {
        throw UnsupportedOperationException("compiled host functions are entered through the object lane")
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
}
