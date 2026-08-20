package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.mapImports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.toChasmResult
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleInstantiator
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor

@InternalChasmApi
suspend fun _instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig,
    taskExecutor: ParallelTaskExecutor,
): ChasmResult<Instance, ChasmError.ExecutionError> {
    if (imports.any { it.value.store !== store.store }) {
        return Error(ChasmError.ExecutionError("Importable belongs to a different Store"))
    }

    return ParallelModuleInstantiator(
        config = config,
        store = store.store,
        module = module.module,
        imports = imports.mapImports(ImportableMapper(store.store)),
        taskExecutor = taskExecutor,
    ).toChasmResult(config, store)
}
