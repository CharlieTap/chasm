package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.internal.InstantiatedModule
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

class FakeManagedInstantiator(
    private val instances: ManagedInstances,
    private val config: RuntimeConfig,
    private val instance: ModuleInstance,
    private val failure: InstantiationError? = null,
) : ManagedInstantiator {

    val requests = ArrayList<Module>()

    override suspend fun instantiate(
        module: Module,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError> {

        requests.add(module)

        if (failure != null) {
            return ChasmResult.Error(failure)
        }

        instances.installed(instance, config)

        return ChasmResult.Success(InstantiatedModule(config, instance))
    }
}
