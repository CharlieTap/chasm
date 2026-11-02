package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.aot.diagnostic.ModuleCompiledFrames
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.internal.instantiate
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

@ExperimentalChasmApi
fun instance(
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<Instance, ChasmError.ExecutionError> =
    instance(
        store = store,
        module = module,
        imports = imports,
        config = config,
        instantiator = ::ModuleInstantiator,
    )

@OptIn(InternalChasmApi::class)
internal inline fun instance(
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
    config: RuntimeConfig,
    crossinline instantiator: ModuleInstantiator,
): ChasmResult<Instance, ChasmError.ExecutionError> {
    val declarations = module.declarations.getOrElse { failure ->
        return Error(ChasmError.ExecutionError("Compiled module rejected: ${failure.message ?: failure}"))
    }
    val trapSource = { instance: ModuleInstance -> CompiledTrapSource(instance, declarations.names, module) }
    return instantiate(store, imports, config, trapSource) { effectiveConfig, runtime, runtimeImports ->
        instantiator(effectiveConfig, runtime, declarations, runtimeImports) { runtimeStore, instance, runtimeTypes, _ ->
            instance.compiledFrames = ModuleCompiledFrames(module)
            ModuleCodeInstallation(module.code::install, runtimeStore, declarations.functionTypeIndices.size, instance, runtimeTypes)
        }
    }
}
