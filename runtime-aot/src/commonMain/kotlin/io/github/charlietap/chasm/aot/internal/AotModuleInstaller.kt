@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
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

internal class AotModuleInstaller(
    private val first: AotModule,
) : ModuleInstaller {

    override fun rejection(module: Module): InstantiationError? {

        val payload = module._payload() as? AotModule
            ?: return ExecutionError.Unsupported("an AOT store only instantiates modules loaded by module(CompiledModule)")

        if (payload === first) {
            return null
        }

        val moduleConfig = payload.config
        val storeConfig = first.config
        val moduleMemory = moduleConfig.limits.memory
        val storeMemory = storeConfig.limits.memory
        val difference = when {
            moduleConfig.limits.cpu != storeConfig.limits.cpu ->
                "fuel ${fuel(moduleConfig.limits.cpu)} but the store has fuel ${fuel(storeConfig.limits.cpu)}"
            moduleConfig.interrupts != storeConfig.interrupts ->
                "interrupts ${moduleConfig.interrupts} but the store has interrupts ${storeConfig.interrupts}"
            moduleMemory.linearMemory != storeMemory.linearMemory ->
                "linearMemory ${bytes(moduleMemory.linearMemory)} but the store allows ${bytes(storeMemory.linearMemory)}"
            moduleMemory.gcHeap != storeMemory.gcHeap ->
                "gcHeap ${bytes(moduleMemory.gcHeap)} but the store allows ${bytes(storeMemory.gcHeap)}"
            moduleMemory.tables != storeMemory.tables ->
                "tables ${bytes(moduleMemory.tables)} but the store allows ${bytes(storeMemory.tables)}"
            moduleMemory.stack != storeMemory.stack ->
                "stack ${moduleMemory.stack ?: "default"} but the store allows ${storeMemory.stack ?: "default"}"
            moduleMemory.onGrowthFailure != storeMemory.onGrowthFailure ->
                "onGrowthFailure ${moduleMemory.onGrowthFailure} but the store has onGrowthFailure ${storeMemory.onGrowthFailure}"
            else -> return null
        }

        return ExecutionError.Unlinkable("the module was compiled with $difference")
    }

    override fun config(
        module: Module,
        storeConfig: RuntimeConfig,
    ): RuntimeConfig {
        return (module._payload() as AotModule).config
    }

    override fun trapSource(
        module: Module,
        instance: ModuleInstance,
    ): TrapSource {

        val payload = module._payload() as AotModule

        return CompiledTrapSource(instance, { payload.declarations.names }) { function ->
            payload.trapSites?.function(function)
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
                classPrefix = payload.classPrefix,
                chunkStarts = { function -> payload.trapSites?.function(function)?.chunkStarts },
                originalLine = { className, methodName, line ->
                    payload.trapLines?.original(className.substringAfterLast('.'), methodName, line) ?: line
                },
                entryClass = compiledEntryClass,
            )

            ModuleCodeInstallation(compiled::install, runtimeStore, payload.declarations.functionTypeIndices.size, instance, runtimeTypes)
        }
    }

    private fun fuel(cpu: CpuLimits): String {
        return when (cpu) {
            is CpuLimits.Metered -> cpu.fuel.units.toString()
            CpuLimits.Unmetered -> "unmetered"
        }
    }

    private fun bytes(value: Bytes?): String {
        return value?.toString() ?: "no limit"
    }
}
