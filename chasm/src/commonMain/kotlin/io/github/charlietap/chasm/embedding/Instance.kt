package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.ComponentExport
import io.github.charlietap.chasm.embedding.shapes.ComponentExportInstance
import io.github.charlietap.chasm.embedding.shapes.ComponentFunction
import io.github.charlietap.chasm.embedding.shapes.ComponentImport
import io.github.charlietap.chasm.embedding.shapes.ComponentInstance
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceType
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.ModuleCompilationCache
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.cachedCompilation
import io.github.charlietap.chasm.embedding.shapes.cachedPreparation
import io.github.charlietap.chasm.embedding.transform.ComponentImportMapper
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.executor.instantiator.CompiledModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.ModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.component.ComponentCompiler
import io.github.charlietap.chasm.executor.instantiator.component.ComponentInstantiator
import io.github.charlietap.chasm.executor.instantiator.component.PreparedComponent
import io.github.charlietap.chasm.executor.instantiator.component.PreparedComponentCoreModuleResolver
import io.github.charlietap.chasm.executor.instantiator.component.linking.ComponentLinker
import io.github.charlietap.chasm.executor.instantiator.component.linking.ResolvedComponentImports
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.address.StoreIdentity
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExport
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExportValue
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.validator.WasmComponentAnalyzer
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport

fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<Instance, ChasmError.ExecutionError> {
    return instance(
        store = store,
        module = module,
        imports = imports,
        config = config,
        compiler = ::ModuleCompiler,
        instantiator = ::ModuleInstantiator,
        importableMapper = ImportableMapper,
    )
}

internal fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig,
    compiler: ModuleCompiler,
    instantiator: CompiledModuleInstantiator,
    importableMapper: Mapper<Importable, ExternalValue>,
): ChasmResult<Instance, ChasmError.ExecutionError> {

    val mappedImports = imports.map { import ->
        RuntimeImport(
            import.moduleName,
            import.entityName,
            importableMapper.map(import.value),
        )
    }

    val compiled = cachedCompilation(
        config = config,
        cache = module.compilationCache,
        compiler = { compiler(config, module.module) },
        cacheCompiled = { cache -> module.compilationCache = cache },
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(
            success = { it },
            failure = { return Error(it) },
        )

    return instantiator(config, store.store, compiled, mappedImports)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .map { internal ->
            Instance(
                config = config,
                instance = internal,
            )
        }.fold(::Success, ::Error)
}

fun instance(
    store: Store,
    component: Component,
    imports: List<ComponentImport>,
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<ComponentInstance, ChasmError> = instance(
    store = store,
    component = component,
    imports = imports,
    config = config,
    analyzer = ::WasmComponentAnalyzer,
    compiler = ::ComponentCompiler,
    importMapper = ::ComponentImportMapper,
    linker = ::ComponentLinker,
    instantiator = ::ComponentInstantiator,
)

internal fun instance(
    store: Store,
    component: Component,
    imports: List<ComponentImport>,
    config: RuntimeConfig,
    analyzer: WasmComponentAnalyzer,
    compiler: ComponentCompiler,
    importMapper: ComponentImportMapper,
    linker: ComponentLinker,
    instantiator: ComponentInstantiator,
): ChasmResult<ComponentInstance, ChasmError> {
    val types = component.analysisCache.types ?: analyzer(component.config, component.component).fold(
        success = { analyzed ->
            component.analysisCache.types = analyzed
            analyzed
        },
        failure = { error -> return Error(ChasmError.ValidationError(error.toString())) },
    )
    val prepared = cachedPreparation(
        config = config,
        cache = component.preparationCache,
        prepare = { compiler(config, component.component, types) },
    ).fold(
        success = { it },
        failure = { error -> return Error(ChasmError.ExecutionError(error.toString())) },
    )
    val mappedImports = importMapper(config, store, imports).fold(
        success = { it },
        failure = { error -> return Error(ChasmError.ExecutionError(error)) },
    )
    val linkedImports = linker(prepared, mappedImports).fold(
        success = { it },
        failure = { error -> return Error(ChasmError.ExecutionError(error.toString())) },
    )
    val componentStore = store.componentStore()
    val root = instantiator(config, store.store, componentStore, prepared, linkedImports).fold(
        success = { it },
        failure = { error -> return Error(ChasmError.ExecutionError(error.toString())) },
    )
    val runtimeInstance = componentStore.liveRoot(root).fold(
        success = { it },
        failure = { error -> return Error(ChasmError.ExecutionError(error.toString())) },
    )
    val resourceTypeAddresses = runtimeInstance.runtimeInfo.resourceTypes.mapValues { (_, index) ->
        RuntimeResourceTypeAddress(runtimeInstance.state.resourceTypes[index.index])
    }

    return Success(
        ComponentInstance(
            config = config,
            store = store.identity,
            root = root,
            exports = runtimeInstance.runtimeInfo.exports.map { export ->
                export.toEmbedding(
                    config,
                    component.config,
                    store.identity,
                    root,
                    runtimeInstance.runtimeInfo.functions,
                    runtimeInstance.state.resourceTypes,
                    resourceTypeAddresses,
                    prepared,
                    linkedImports,
                )
            },
        ),
    )
}

private fun PreparedComponentExport.toEmbedding(
    config: RuntimeConfig,
    componentConfig: ComponentConfig,
    store: StoreIdentity,
    root: ComponentRootAddress,
    functions: List<PreparedComponentFunction>,
    resourceTypes: IntArray,
    resourceTypeAddresses: Map<ComponentResourceTypeId, RuntimeResourceTypeAddress>,
    prepared: PreparedComponent,
    imports: ResolvedComponentImports,
): ComponentExport = ComponentExport(
    name = name,
    value = when (val export = value) {
        is PreparedComponentExportValue.CoreModule -> {
            val compiled = PreparedComponentCoreModuleResolver(prepared, imports, export.module)
            Module(
                config = componentConfig.moduleConfig,
                module = requireNotNull(compiled.sourceModule),
                compilationCache = ModuleCompilationCache(config, compiled),
            )
        }
        is PreparedComponentExportValue.Function -> ComponentFunction(
            config = config,
            store = store,
            root = root,
            function = export.function,
            type = functions[export.function.index].type(),
            resourceTypes = resourceTypeAddresses,
        )
        is PreparedComponentExportValue.Instance -> ComponentExportInstance(
            export.exports.map { child ->
                child.toEmbedding(
                    config,
                    componentConfig,
                    store,
                    root,
                    functions,
                    resourceTypes,
                    resourceTypeAddresses,
                    prepared,
                    imports,
                )
            },
        )
        is PreparedComponentExportValue.ResourceType -> ComponentResourceType(
            store = store,
            address = RuntimeResourceTypeAddress(resourceTypes[export.resourceType.index]),
        )
    },
)

private fun PreparedComponentFunction.type() = when (this) {
    is PreparedComponentFunction.HostImport -> functionType
    is PreparedComponentFunction.LiftedCore -> liftPlan.functionType
}
