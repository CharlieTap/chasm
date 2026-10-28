package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.embedding.diagnostic.Instantiation
import io.github.charlietap.chasm.embedding.diagnostic.InterpreterTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.diagnostic.executionError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.ext.withStoreSettings
import io.github.charlietap.chasm.embedding.internal.ModuleCodeInstaller
import io.github.charlietap.chasm.embedding.internal.StartFunctionInvoker
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.executor.instantiator.ModuleCodeInstaller
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.instance.ModuleInstance as RuntimeModuleInstance

fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<Instance, ChasmError.ExecutionError> = instance(
    store = store,
    declarations = module.declarations,
    imports = imports,
    config = config,
    instantiator = ::ModuleInstantiator,
    codeInstaller = ModuleCodeInstaller(module.module),
    importableMapper = ImportableMapper(store.store),
    trapSource = { instance -> InterpreterTrapSource(instance, module) },
)

internal fun instance(
    store: Store,
    declarations: ModuleDeclarations,
    imports: List<Import>,
    config: RuntimeConfig,
    instantiator: ModuleInstantiator,
    codeInstaller: ModuleCodeInstaller,
    importableMapper: Mapper<Importable, ExternalValue>,
    trapSource: (RuntimeModuleInstance) -> TrapSource?,
): ChasmResult<Instance, ChasmError.ExecutionError> {

    if (imports.any { it.value.store !== store.store }) {
        return Error(ChasmError.ExecutionError("Importable belongs to a different Store"))
    }

    val mappedImports = imports.mapImports(importableMapper)
    val firstFunctionAddress = store.store.functions.size
    val effectiveConfig = config.withStoreSettings(store.store.config)

    return instantiator(effectiveConfig, store.store, declarations, mappedImports, codeInstaller, ::StartFunctionInvoker)
        .toChasmResult(effectiveConfig, store, firstFunctionAddress, trapSource)
}

internal fun List<Import>.mapImports(
    importableMapper: Mapper<Importable, ExternalValue>,
): List<RuntimeImport> = map { import ->
    RuntimeImport(
        import.moduleName,
        import.entityName,
        importableMapper.map(import.value),
    )
}

internal fun Result<RuntimeModuleInstance, ModuleTrapError>.toChasmResult(
    config: RuntimeConfig,
    store: Store,
    firstFunctionAddress: Int,
    trapSource: (RuntimeModuleInstance) -> TrapSource?,
): ChasmResult<Instance, ChasmError.ExecutionError> =
    this
        .mapError { error -> executionError(store, error, Instantiation(firstFunctionAddress, trapSource)) }
        .map { internal ->
            if (config.debugInfo) store.diagnostics.register(trapSource(internal))
            Instance(
                config = config,
                instance = internal,
                store = store.store,
            )
        }.fold(::Success, ::Error)
