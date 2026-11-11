@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.internal.instantiate
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._binary
import io.github.charlietap.chasm.embedding.shapes._config
import io.github.charlietap.chasm.embedding.shapes._declarations
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.jit.internal.CompileError.Stage
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

@InternalChasmApi
fun _instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig,
    cache: CompilationCache? = null,
): ChasmResult<Instance, ChasmError.ExecutionError> =
    instance(
        store = store,
        module = module,
        imports = imports,
        config = config,
        compiler = { compiled, compileConfig -> compile(compiled, compileConfig, cache) },
        instantiator = ::ModuleInstantiator,
    )

internal inline fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    config: RuntimeConfig,
    crossinline compiler: (Module, CompileConfig) -> CompileResult,
    crossinline instantiator: ModuleInstantiator,
): ChasmResult<Instance, ChasmError.ExecutionError> {
    val storeConfig = store.store.config
    val compilation = when (val compiled = compiler(module, CompileConfig(storeConfig.meterFuel, storeConfig.interruptible, config.debugInfo))) {
        is CompileResult.Success -> compiled.compilation
        is CompileResult.Error -> return ChasmResult.Error(ChasmError.ExecutionError(compiled.error.message()))
    }
    val declarations = module._declarations()
    val diagnostics = if (config.debugInfo) JitTrapDiagnostics(module._binary(), module._config(), compilation.chunkStarts) else null
    val trapSource = { instance: ModuleInstance ->
        CompiledTrapSource(instance, { diagnostics?.names ?: declarations.names }) { function -> diagnostics?.functionTrapSites(function) }
    }
    return try {
        instantiate(store, imports, config, trapSource) { effectiveConfig, runtime, runtimeImports ->
            instantiator(effectiveConfig, runtime, declarations, runtimeImports) { runtimeStore, instance, runtimeTypes, _ ->
                instance.compiledFrames = ModuleCompiledFrames(
                    packageName = compilation.packageName,
                    chunkStarts = { function -> compilation.chunkStarts[function] },
                    originalLine = { _, _, line -> line },
                )
                ModuleCodeInstallation(compilation::install, runtimeStore, declarations.functionTypeIndices.size, instance, runtimeTypes)
            }
        }
    } catch (failure: LinkageError) {
        ChasmResult.Error(ChasmError.ExecutionError(CompileError(failure.toString(), Stage.LOADING).message()))
    }
}
