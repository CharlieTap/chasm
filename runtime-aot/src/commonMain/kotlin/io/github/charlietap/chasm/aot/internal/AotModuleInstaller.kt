@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.Capabilities
import io.github.charlietap.chasm.aot.CompiledModuleMetadata
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.internal.ModuleInstaller
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.invoker.compiled.compiledEntryClass
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class AotModuleInstaller : ModuleInstaller {

    override fun rejection(
        module: Module,
        store: StoreConfig,
    ): String? {

        val payload = module._payload() as? AotModule
            ?: return "an AOT store only instantiates modules loaded by compiledModule()"

        return mismatch(payload.metadata.capabilities, store)?.let { reason -> "Compiled module rejected: $reason" }
    }

    override fun config(
        module: Module,
        requested: RuntimeConfig,
    ): RuntimeConfig {
        return runtimeConfig((module._payload() as AotModule).metadata)
    }

    override fun trapSource(
        module: Module,
        instance: ModuleInstance,
    ): TrapSource {

        val payload = module._payload() as AotModule

        return CompiledTrapSource(instance, { payload.declarations.names }) { function ->
            payload.compiled.trapSites?.function(function)
        }
    }

    override suspend fun instantiate(
        module: Module,
        config: RuntimeConfig,
        runtime: RuntimeStore,
        imports: List<RuntimeImport>,
    ): Result<ModuleInstance, ModuleTrapError> {

        val payload = module._payload() as AotModule
        val compiled = payload.compiled

        return ModuleInstantiator(config, runtime, payload.declarations, imports) { runtimeStore, instance, runtimeTypes, _ ->

            instance.compiledFrames = ModuleCompiledFrames(
                packageName = compiled.packageName,
                chunkStarts = { function -> compiled.trapSites?.function(function)?.chunkStarts },
                originalLine = { className, methodName, line ->
                    compiled.trapLines?.original(className.substringAfterLast('.'), methodName, line) ?: line
                },
                entryClass = compiledEntryClass,
            )

            ModuleCodeInstallation(compiled.code::install, runtimeStore, payload.declarations.functionTypeIndices.size, instance, runtimeTypes)
        }
    }

    private fun mismatch(
        capabilities: Capabilities,
        store: StoreConfig,
    ): String? {
        return when {
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
    }

    @Suppress("DEPRECATION")
    private fun runtimeConfig(metadata: CompiledModuleMetadata): RuntimeConfig {

        val defaults = RuntimeConfig()

        return RuntimeConfig(
            debugInfo = metadata.capabilities.stacktraces,
            gcStrategy = metadata.storeConfig.gcStrategy ?: defaults.gcStrategy,
            gcThreshold = metadata.storeConfig.gcThreshold ?: defaults.gcThreshold,
        )
    }
}
