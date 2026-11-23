package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.internal.ModuleInstallation
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.managedEntryClass
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class JitModuleInstallation(
    module: DecodedModule,
    private val compilation: JitCompilation,
    private val shapes: StoreShapes,
    private val managedStore: ManagedStore,
    private val handles: JitHandles,
    stacktraces: Boolean,
) : ModuleInstallation {

    private val declarations = module.declarations

    private val diagnostics = if (stacktraces) {
        JitTrapDiagnostics(module.binary, module.config, compilation.chunkStarts)
    } else {
        null
    }

    override fun external(importable: Importable): ExternalValue {
        return when (val payload = importable._payload()) {
            is JitMemory -> ExternalValue.Memory(payload.address)
            is JitTable -> ExternalValue.Table(payload.address)
            is JitGlobal -> ExternalValue.Global(payload.address)
            else -> payload as ExternalValue
        }
    }

    override fun config(storeConfig: RuntimeConfig): RuntimeConfig {
        return storeConfig
    }

    override fun trapSource(instance: ModuleInstance): TrapSource {
        return CompiledTrapSource(instance, ::names, ::functionTrapSites)
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
            classPrefix = "${compilation.packageName}.",
            chunkStarts = { function -> compilation.chunkStarts[function] },
            originalLine = { _, _, line -> line },
            entryClass = managedEntryClass,
        )

        return ModuleCodeInstallation(
            install = { installation -> install(config, instance, installation) },
            store = store,
            definedFunctions = declarations.functionTypeIndices.size,
            instance = instance,
            runtimeTypes = runtimeTypes,
        )
    }

    private fun install(
        config: RuntimeConfig,
        instance: ModuleInstance,
        installation: CodeInstallation,
    ) {

        declarations.definedTypes.forEachIndexed { index, definedType ->
            val shape = compilation.shapeTypes.getOrNull(index)?.let(shapes::loadClass)
            managedStore.register(instance.runtimeTypes[index].value, definedType, shape)
        }

        handles.installed(instance, config)
        compilation.install(installation)
    }

    private fun names(): ModuleNames {
        return diagnostics?.names ?: declarations.names
    }

    private fun functionTrapSites(function: Int): FunctionTrapSites? {
        return diagnostics?.functionTrapSites(function)
    }
}
