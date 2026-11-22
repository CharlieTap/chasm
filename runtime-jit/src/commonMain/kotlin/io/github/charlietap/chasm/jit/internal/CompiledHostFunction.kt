package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.runtime.compiled.gc.ObjectLaneHostFunction
import io.github.charlietap.chasm.runtime.host.RawHostModuleInstance
import io.github.charlietap.chasm.runtime.host.RawHostResources
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.FunctionType

internal class CompiledHostFunction(
    type: FunctionType,
    private val function: HostFunction,
    private val handles: JitHandles,
    private val hostResources: HostResources,
) : ObjectLaneHostFunction(type) {

    override fun call(
        stack: LongArray,
        references: Array<Any?>,
        parameters: Int,
        results: Int,
        caller: RawHostModuleInstance,
        resources: RawHostResources,
    ) {
        context(stack, references, handles.contextOf(caller as ModuleInstance), hostResources) {
            function.invoke(parameters, results)
        }
    }
}
