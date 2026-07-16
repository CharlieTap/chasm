package io.github.charlietap.chasm.embedding.transform

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.ComponentExportInstance
import io.github.charlietap.chasm.embedding.shapes.ComponentFunction
import io.github.charlietap.chasm.embedding.shapes.ComponentImport
import io.github.charlietap.chasm.embedding.shapes.ComponentImportable
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceType
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.cachedCompilation
import io.github.charlietap.chasm.executor.instantiator.ModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.component.linking.ComponentLinkInput
import io.github.charlietap.chasm.executor.instantiator.component.linking.NamedComponentLinkInput
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal typealias ComponentImportMapper = (
    RuntimeConfig,
    Store,
    List<ComponentImport>,
) -> Result<List<NamedComponentLinkInput>, String>

internal fun ComponentImportMapper(
    config: RuntimeConfig,
    store: Store,
    imports: List<ComponentImport>,
): Result<List<NamedComponentLinkInput>, String> = ComponentImportMapper(
    config = config,
    store = store,
    imports = imports,
    moduleCompiler = ::ModuleCompiler,
    hostFunctionMapper = ::ComponentHostFunctionMapper,
)

internal inline fun ComponentImportMapper(
    config: RuntimeConfig,
    store: Store,
    imports: List<ComponentImport>,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline hostFunctionMapper: (Store, io.github.charlietap.chasm.embedding.shapes.ComponentHostFunction) -> RuntimeComponentHostFunction,
): Result<List<NamedComponentLinkInput>, String> = mapComponentImports(
    config = config,
    store = store,
    imports = imports,
    moduleCompiler = { runtimeConfig, module -> moduleCompiler(runtimeConfig, module) },
    hostFunctionMapper = { functionStore, function -> hostFunctionMapper(functionStore, function) },
)

@PublishedApi
internal fun mapComponentImports(
    config: RuntimeConfig,
    store: Store,
    imports: List<ComponentImport>,
    moduleCompiler: ModuleCompiler,
    hostFunctionMapper: (Store, io.github.charlietap.chasm.embedding.shapes.ComponentHostFunction) -> RuntimeComponentHostFunction,
): Result<List<NamedComponentLinkInput>, String> = binding {
    imports.map { import ->
        val value = when (val importable = import.value) {
            is ComponentFunction -> {
                if (importable.store !== store.identity) {
                    Err("component function belongs to another store").bind()
                }
                ComponentLinkInput.Function(
                    function = RuntimeComponentHostFunction.Linked(importable.root, importable.function),
                    type = importable.type,
                    resourceTypes = importable.resourceTypes,
                )
            }
            is ComponentExportInstance -> ComponentLinkInput.Instance(
                mapComponentImports(
                    config = config,
                    store = store,
                    imports = importable.exports.map { export -> ComponentImport(export.name, export.value) },
                    moduleCompiler = moduleCompiler,
                    hostFunctionMapper = hostFunctionMapper,
                ).bind(),
            )
            is Module -> ComponentLinkInput.CoreModule(
                cachedCompilation(
                    config = config,
                    cache = importable.compilationCache,
                    compiler = { moduleCompiler(config, importable.module) },
                    cacheCompiled = { cache -> importable.compilationCache = cache },
                ).mapError(ModuleTrapError::toString).bind(),
            )
            is ComponentResourceType -> {
                if (importable.store !== store.identity) {
                    Err("component resource type belongs to another store").bind()
                }
                ComponentLinkInput.ResourceType(importable.address)
            }
            is ComponentImportable.Function -> ComponentLinkInput.Function(
                hostFunctionMapper(store, importable.function),
            )
            is ComponentImportable.Instance -> ComponentLinkInput.Instance(
                mapComponentImports(
                    config = config,
                    store = store,
                    imports = importable.imports,
                    moduleCompiler = moduleCompiler,
                    hostFunctionMapper = hostFunctionMapper,
                ).bind(),
            )
            is ComponentImportable.CoreModule -> ComponentLinkInput.CoreModule(
                cachedCompilation(
                    config = config,
                    cache = importable.module.compilationCache,
                    compiler = { moduleCompiler(config, importable.module.module) },
                    cacheCompiled = { cache -> importable.module.compilationCache = cache },
                ).mapError(ModuleTrapError::toString).bind(),
            )
            is ComponentImportable.ResourceType -> {
                if (importable.type.store !== store.identity) {
                    Err("component resource type belongs to another store").bind()
                }
                ComponentLinkInput.ResourceType(importable.type.address)
            }
        }
        NamedComponentLinkInput(import.name, value)
    }
}
