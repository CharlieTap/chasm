package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.Instantiation
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.diagnostic.executionError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.ext.withStoreSettings
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
inline fun instantiate(
    store: Store,
    imports: List<Import>,
    config: RuntimeConfig,
    invoker: InstanceInvoker,
    noinline trapSource: (ModuleInstance) -> TrapSource?,
    instantiator: (RuntimeConfig, RuntimeStore, List<RuntimeImport>) -> Result<ModuleInstance, ModuleTrapError>,
): ChasmResult<Instance, ChasmError.ExecutionError> {
    if (importsFromAnotherStore(store, imports)) {
        return Error(ChasmError.ExecutionError("Importable belongs to a different Store"))
    }
    val runtime = store.store
    val firstFunctionAddress = runtime.functions.size
    val effectiveConfig = config.withStoreSettings(runtime.config)
    val result = instantiator(effectiveConfig, runtime, runtimeImports(store, imports))
    return wrapInstantiationResult(result, effectiveConfig, store, firstFunctionAddress, invoker, trapSource)
}

@PublishedApi
internal fun importsFromAnotherStore(
    store: Store,
    imports: List<Import>,
): Boolean = imports.any { import -> import.value.store !== store.store }

@PublishedApi
internal fun runtimeImports(
    store: Store,
    imports: List<Import>,
): List<RuntimeImport> {
    val mapper = ImportableMapper(store.store)
    return imports.map { import -> RuntimeImport(import.moduleName, import.entityName, mapper.map(import.value)) }
}

@PublishedApi
internal fun wrapInstantiationResult(
    result: Result<ModuleInstance, ModuleTrapError>,
    config: RuntimeConfig,
    store: Store,
    firstFunctionAddress: Int,
    invoker: InstanceInvoker,
    trapSource: (ModuleInstance) -> TrapSource?,
): ChasmResult<Instance, ChasmError.ExecutionError> =
    result
        .mapError { error -> executionError(store, error, Instantiation(firstFunctionAddress, trapSource)) }
        .map { instance ->
            if (config.debugInfo) store.diagnostics.register(trapSource(instance))
            Instance(
                config = config,
                instance = instance,
                store = store.store,
                invoker = invoker,
            )
        }.fold(::Success, ::Error)
