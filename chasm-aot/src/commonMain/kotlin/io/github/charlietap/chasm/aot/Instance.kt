package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.aot.internal.CompiledInvoker
import io.github.charlietap.chasm.aot.internal.CompiledStartInvoker
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.internal.instantiate
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.StartFunctionInvoker
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation

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
        startInvoker = ::CompiledStartInvoker,
    )

@OptIn(InternalChasmApi::class)
internal inline fun instance(
    store: Store,
    module: CompiledModule,
    imports: List<Import>,
    config: RuntimeConfig,
    crossinline instantiator: ModuleInstantiator,
    noinline startInvoker: StartFunctionInvoker,
): ChasmResult<Instance, ChasmError.ExecutionError> {
    val declarations = when (val compatible = module.compatibleDeclarations(store.store.config)) {
        is ChasmResult.Success -> compatible.result
        is ChasmResult.Error -> return compatible
    }
    return instantiate(store, imports, config, CompiledInvoker, { instance -> CompiledTrapSource(instance, declarations.names) }) { effectiveConfig, runtime, runtimeImports ->
        instantiator(
            effectiveConfig,
            runtime,
            declarations,
            runtimeImports,
            { runtimeStore, instance, runtimeTypes, _ ->
                ModuleCodeInstallation(module::install, runtimeStore, declarations.functionTypeIndices.size, instance, runtimeTypes)
            },
            startInvoker,
        )
    }
}
