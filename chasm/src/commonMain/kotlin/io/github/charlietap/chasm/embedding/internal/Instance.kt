package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.InterpreterTrapSource
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._declarations
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleInstantiator
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.runtime.value.ReferenceValue

@InternalChasmApi
suspend fun _instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig,
    taskExecutor: ParallelTaskExecutor,
): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {

    require(!holdsManagedReferences(store)) { MIXED_STORE_ERROR }

    return instantiate(
        store = store,
        imports = imports,
        config = config,
        trapSource = { instance -> InterpreterTrapSource(instance, module) },
    ) { effectiveConfig, runtime, runtimeImports ->
        ParallelModuleInstantiator(
            config = effectiveConfig,
            store = runtime,
            module = module._declarations(),
            imports = runtimeImports,
            taskExecutor = taskExecutor,
            codeInstaller = ParallelModuleCodeInstaller(module._module()),
        )
    }
}
