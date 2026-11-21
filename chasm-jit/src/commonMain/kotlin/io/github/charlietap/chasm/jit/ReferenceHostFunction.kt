@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostParameters
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostResults
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.jit.internal.ObjectLaneCallback
import io.github.charlietap.chasm.jit.internal._objectLaneFunction
import io.github.charlietap.chasm.type.FunctionType

@ExperimentalChasmApi
fun interface ReferenceHostFunction {

    context(
        _: HostStack,
        _: ReferenceSlots,
        _: HostModuleInstance,
        _: HostResources,
    )
    fun invoke(parameters: HostParameters, results: HostResults)
}

@ExperimentalChasmApi
fun referenceFunction(
    store: Store,
    type: FunctionType,
    function: ReferenceHostFunction,
): Function {
    return _objectLaneFunction(
        store,
        type,
        ObjectLaneCallback { stack, references, parameters, results, caller, resources ->
            context(stack, ReferenceSlots(references), caller, resources) {
                function.invoke(parameters, results)
            }
        },
    )
}
