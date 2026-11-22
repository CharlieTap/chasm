package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.Instantiation
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.diagnostic.instantiationError
import io.github.charlietap.chasm.embedding.diagnostic.pendingException
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.ext.withStoreSettings
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
inline fun instantiate(
    store: Store,
    imports: List<Import>,
    config: RuntimeConfig,
    noinline trapSource: (ModuleInstance) -> TrapSource?,
    instantiator: (RuntimeConfig, RuntimeStore, List<RuntimeImport>) -> Result<ModuleInstance, ModuleTrapError>,
): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {

    val runtime = checkNotNull(liveRuntime(store)) { InvocationError.StoreDropped.toString() }
    require(!importsFromAnotherStore(store, imports)) { "Importable belongs to a different Store" }

    if (runtime.invocationDepth == 0) {
        runtime.heap.clearPendingException()
    }

    val firstFunctionAddress = runtime.functions.size
    val effectiveConfig = config.withStoreSettings(runtime.config)
    val result = instantiator(effectiveConfig, runtime, runtimeImports(store, imports))
    return wrapInstantiationResult(result, effectiveConfig, store, firstFunctionAddress, trapSource)
}

@PublishedApi
internal fun liveRuntime(
    store: Store,
): RuntimeStore? = store.store.takeUnless { store.isDropped }

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
    trapSource: (ModuleInstance) -> TrapSource?,
): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {
    return result.fold(
        success = { instance ->

            if (config.debugInfo) {
                store.diagnostics.register(trapSource(instance))
            }

            Success(Instance(config, instance, store.store))
        },
        failure = { error ->
            val instantiation = Instantiation(firstFunctionAddress, trapSource)
            Error(instantiationError(store, error, instantiation) { pendingException(store) })
        },
    )
}

@InternalChasmApi
fun holdsManagedReferences(
    store: Store,
): Boolean {
    return store.store.referenceStorage != null
}

@InternalChasmApi
fun holdsWasmFunctions(
    store: Store,
): Boolean {
    return store.store.functions.any { function -> function is FunctionInstance.WasmFunction }
}

@InternalChasmApi
const val MIXED_STORE_ERROR = "a store cannot hold chasm-jit instances alongside interpreter or AOT instances"
