package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.HostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction as InternalHostFunction

internal object HostFunctionMapper : Mapper<HostFunction, InternalHostFunction> {

    override fun map(input: HostFunction): InternalHostFunction {
        return { params: List<ExecutionValue> ->
            val store = Store(this.store)
            val instance = Instance(this.config, this.instance)
            val results = with(HostFunctionContext(store, instance)) {
                input(params)
            }
            results
        }
    }
}
