package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.internal.MIXED_STORE_ERROR
import io.github.charlietap.chasm.embedding.internal.holdsManagedReferences
import io.github.charlietap.chasm.embedding.internal.instantiate
import io.github.charlietap.chasm.embedding.internal.storeDropped
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.value.ReferenceValue

@ExperimentalChasmApi
fun instance(
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {
    return instance(
        store = store,
        module = module,
        imports = imports,
        instantiator = ::ModuleInstantiator,
    )
}

@OptIn(InternalChasmApi::class)
internal inline fun instance(
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
    crossinline instantiator: ModuleInstantiator,
): ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>> {

    check(!storeDropped(store)) { InvocationError.StoreDropped.toString() }
    require(!holdsManagedReferences(store)) { MIXED_STORE_ERROR }

    val metadata = module.metadataResult.getOrElse { failure -> return unsupported(failure.message ?: failure.toString()) }
    mismatch(metadata.capabilities, store.store.config)?.let { reason -> return unsupported(reason) }
    val declarations = module.declarations.getOrElse { failure -> return unsupported(failure.message ?: failure.toString()) }
    val trapSource = { instance: ModuleInstance ->
        CompiledTrapSource(instance, { declarations.names }) { function -> module.trapSites?.function(function) }
    }

    return instantiate(store, imports, runtimeConfig(metadata), trapSource) { effectiveConfig, runtime, runtimeImports ->
        instantiator(effectiveConfig, runtime, declarations, runtimeImports) { runtimeStore, instance, runtimeTypes, _ ->
            instance.compiledFrames = ModuleCompiledFrames(
                packageName = module.packageName,
                chunkStarts = { function -> module.trapSites?.function(function)?.chunkStarts },
                originalLine = { className, methodName, line -> module.trapLines?.original(className.substringAfterLast('.'), methodName, line) ?: line },
            )
            ModuleCodeInstallation(module.code::install, runtimeStore, declarations.functionTypeIndices.size, instance, runtimeTypes)
        }
    }
}

private fun unsupported(reason: String): Error<ExecutionError.Unsupported> {
    return Error(ExecutionError.Unsupported("Compiled module rejected: $reason"))
}

private fun mismatch(
    capabilities: Capabilities,
    store: StoreConfig,
): String? = when {
    capabilities.meterFuel && !store.meterFuel ->
        "the module was compiled with fuel checks but the store does not meter fuel"
    !capabilities.meterFuel && store.meterFuel ->
        "the store meters fuel but the module was compiled without fuel checks"
    capabilities.interruptible && !store.interruptible ->
        "the module was compiled with interrupt checks but the store is not interruptible"
    !capabilities.interruptible && store.interruptible ->
        "the store is interruptible but the module was compiled without interrupt checks"
    else -> null
}

@Suppress("DEPRECATION")
private fun runtimeConfig(
    metadata: CompiledModuleMetadata,
): RuntimeConfig {
    val defaults = RuntimeConfig()
    return RuntimeConfig(
        debugInfo = metadata.capabilities.stacktraces,
        gcStrategy = metadata.storeConfig.gcStrategy ?: defaults.gcStrategy,
        gcThreshold = metadata.storeConfig.gcThreshold ?: defaults.gcThreshold,
    )
}
