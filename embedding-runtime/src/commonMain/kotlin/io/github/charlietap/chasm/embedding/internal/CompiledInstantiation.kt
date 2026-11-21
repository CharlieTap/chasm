package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.Instantiation
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.diagnostic.instantiationError
import io.github.charlietap.chasm.embedding.ext.withStoreSettings
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.embedding.error.InstantiationError as CompiledInstantiationError
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
fun <E> instantiateCompiled(
    store: Store,
    imports: List<Import>,
    config: RuntimeConfig,
    trapSource: (ModuleInstance) -> TrapSource?,
    thrown: () -> E,
    instantiator: (RuntimeConfig, RuntimeStore, List<RuntimeImport>) -> Result<ModuleInstance, ModuleTrapError>,
): ChasmResult<Instance, CompiledInstantiationError<E>> {

    val runtime = checkNotNull(liveRuntime(store)) { InvocationError.StoreDropped.toString() }

    require(!importsFromAnotherStore(store, imports)) { "an import belongs to a different store" }

    val firstFunctionAddress = runtime.functions.size
    val effectiveConfig = config.withStoreSettings(runtime.config)
    return instantiator(effectiveConfig, runtime, runtimeImports(store, imports)).fold(
        success = { instance ->
            if (effectiveConfig.debugInfo) {
                store.diagnostics.register(trapSource(instance))
            }
            ChasmResult.Success(Instance(effectiveConfig, instance, store.store))
        },
        failure = { error ->
            ChasmResult.Error(instantiationError(store, error, Instantiation(firstFunctionAddress, trapSource), thrown))
        },
    )
}

@InternalChasmApi
fun embeddingInstance(
    store: Store,
    config: RuntimeConfig,
    module: ModuleInstance,
): Instance {
    return Instance(config, module, store.store)
}

@InternalChasmApi
fun importable(
    store: Store,
    value: ExternalValue,
): Importable {
    return ImportableMapper(store.store).bimap(value)
}

@InternalChasmApi
fun externalValue(importable: Importable): ExternalValue {
    return ImportableMapper(importable.store).map(importable)
}
