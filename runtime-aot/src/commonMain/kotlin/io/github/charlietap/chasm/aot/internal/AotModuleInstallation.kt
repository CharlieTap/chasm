@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.internal.ModuleInstallation
import io.github.charlietap.chasm.embedding.managed.ManagedGlobal
import io.github.charlietap.chasm.embedding.managed.ManagedInstances
import io.github.charlietap.chasm.embedding.managed.ManagedMemory
import io.github.charlietap.chasm.embedding.managed.ManagedTable
import io.github.charlietap.chasm.embedding.managed.runStartFunction
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.RttRegistry
import io.github.charlietap.chasm.runtime.compiled.gc.managedEntryClass
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class AotModuleInstallation(
    private val payload: AotModule,
    private val managedStore: ManagedStore,
    private val registry: RttRegistry,
    private val instances: ManagedInstances,
) : ModuleInstallation {

    private val declarations = payload.declarations

    override fun external(importable: Importable): ExternalValue {
        return when (val external = importable._payload()) {
            is ManagedMemory -> ExternalValue.Memory(external.address)
            is ManagedTable -> ExternalValue.Table(external.address)
            is ManagedGlobal -> ExternalValue.Global(external.address)
            else -> external as ExternalValue
        }
    }

    override fun config(storeConfig: RuntimeConfig): RuntimeConfig {
        return payload.config
    }

    override fun trapSource(instance: ModuleInstance): TrapSource {
        return CompiledTrapSource(instance, { declarations.names }) { function ->
            payload.trapSites?.function(function)
        }
    }

    override fun thrown(): ExceptionReference {
        return managedStore.pending.take()
    }

    override suspend fun instantiate(
        config: RuntimeConfig,
        runtime: RuntimeStore,
        imports: List<RuntimeImport>,
    ): Result<ModuleInstance, ModuleTrapError> {
        return ModuleInstantiator(
            config = config,
            store = runtime,
            module = declarations,
            imports = imports,
            codeInstaller = { store, instance, runtimeTypes, _ -> installCode(config, store, instance, runtimeTypes) },
            startInvoker = ::runStartFunction,
        )
    }

    private fun installCode(
        config: RuntimeConfig,
        store: RuntimeStore,
        instance: ModuleInstance,
        runtimeTypes: RuntimeTypeMap,
    ): Result<Unit, ModuleTrapError> {

        instance.compiledFrames = ModuleCompiledFrames(
            classPrefix = payload.classPrefix,
            chunkStarts = { function -> payload.trapSites?.function(function)?.chunkStarts },
            originalLine = { className, methodName, line ->
                payload.trapLines?.original(className.substringAfterLast('.'), methodName, line) ?: line
            },
            entryClass = managedEntryClass,
        )

        declarations.definedTypes.forEachIndexed { index, definedType ->
            managedStore.register(runtimeTypes[index].value, definedType)
        }

        val rtts = registry.register(declarations.definedTypes, declarations.rttKeys, runtimeTypes) { index ->
            payload.structLayouts.layout(index)
        }

        return ModuleCodeInstallation(
            install = { installation ->
                instances.installed(instance, config)
                payload.compiled.install(installation)
            },
            store = store,
            definedFunctions = declarations.functionTypeIndices.size,
            instance = instance,
            runtimeTypes = runtimeTypes,
            rtts = rtts,
        )
    }
}
