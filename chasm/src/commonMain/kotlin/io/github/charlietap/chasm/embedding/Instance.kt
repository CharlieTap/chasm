@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.embedding.diagnostic.InterpreterTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.internal.InterpreterInvoker
import io.github.charlietap.chasm.embedding.internal.ModuleCodeInstaller
import io.github.charlietap.chasm.embedding.internal.StartFunctionInvoker
import io.github.charlietap.chasm.embedding.internal.instantiate
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.ModuleCodeInstaller
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
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
    trapSource = { instance -> InterpreterTrapSource(instance, module) },
)

internal fun instance(
    store: Store,
    declarations: ModuleDeclarations,
    imports: List<Import>,
    config: RuntimeConfig,
    instantiator: ModuleInstantiator,
    codeInstaller: ModuleCodeInstaller,
    trapSource: (RuntimeModuleInstance) -> TrapSource?,
): ChasmResult<Instance, ChasmError.ExecutionError> =
    instantiate(store, imports, config, InterpreterInvoker, trapSource) { effectiveConfig, runtime, runtimeImports ->
        instantiator(effectiveConfig, runtime, declarations, runtimeImports, codeInstaller, ::StartFunctionInvoker)
    }
