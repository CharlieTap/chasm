package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.CompiledTrapSource
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.internal.embeddingInstance
import io.github.charlietap.chasm.embedding.internal.instantiateCompiled
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
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCodeInstallation
import io.github.charlietap.chasm.runtime.compiled.ModuleCompiledFrames
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedReferenceBridge
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.DefinedType

internal class JitInstantiator(
    private val store: Store,
    private val bridge: ManagedReferenceBridge,
    private val handles: JitHandles,
) {

    suspend fun instantiate(
        module: Module,
        imports: List<Import>,
        config: RuntimeConfig,
    ): ChasmResult<Instance, InstantiationError<ExceptionReference>> {

        val storeConfig = store.store.config
        val shapes = StoreShapes.of(store.store, CodeInstallation::class.java.classLoader!!)
        val compilation = when (val compiled = compile(module, CompileConfig(storeConfig.meterFuel, storeConfig.interruptible, config.debugInfo, shapes))) {
            is CompileResult.Success -> compiled.compilation
            is CompileResult.Error -> return ChasmResult.Error(ExecutionError.Unsupported(compiled.error.message()))
        }
        val declarations = module._declarations()
        val diagnostics = if (config.debugInfo) JitTrapDiagnostics(module._binary(), module._config(), compilation.chunkStarts) else null
        val trapSource = { instance: ModuleInstance ->
            CompiledTrapSource(instance, { diagnostics?.names ?: declarations.names }) { function -> diagnostics?.functionTrapSites(function) }
        }

        return try {
            instantiateCompiled(store, imports, config, trapSource, bridge::take) { effectiveConfig, runtime, runtimeImports ->
                ModuleInstantiator(effectiveConfig, runtime, declarations, runtimeImports) { runtimeStore, instance, runtimeTypes, _ ->
                    instance.compiledFrames = ModuleCompiledFrames(
                        packageName = compilation.packageName,
                        chunkStarts = { function -> compilation.chunkStarts[function] },
                        originalLine = { _, _, line -> line },
                    )
                    val install = { installation: CodeInstallation ->
                        registerShapes(shapes, instance, declarations.definedTypes, compilation.shapeTypes)
                        handles.installed(instance, embeddingInstance(store, effectiveConfig, instance))
                        compilation.install(installation)
                    }
                    ModuleCodeInstallation(install, runtimeStore, declarations.functionTypeIndices.size, instance, runtimeTypes)
                }
            }
        } catch (failure: LinkageError) {
            ChasmResult.Error(ExecutionError.Unsupported(CompileError(failure.toString(), Stage.LOADING).message()))
        } catch (_: OutOfMemoryError) {
            val message = InvocationError.GuestHeapOutOfMemory.toString()
            ChasmResult.Error(ExecutionError.Trapped(Trap(TrapReason.GUEST_HEAP_EXHAUSTED, message, null, null)))
        }
    }

    private fun registerShapes(
        shapes: StoreShapes,
        instance: ModuleInstance,
        definedTypes: List<DefinedType>,
        shapeTypes: List<String?>,
    ) {
        definedTypes.forEachIndexed { index, definedType ->
            val shape = shapeTypes.getOrNull(index)?.let(shapes::loadClass)
            bridge.register(instance.runtimeTypes[index].value, definedType, shape)
        }
    }
}
