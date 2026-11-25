package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleInstantiator
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class InterpreterModuleInstallation(
    private val decodedModule: DecodedModule,
    private val taskExecutor: ParallelTaskExecutor,
    private val trapSources: TrapSources,
    private val handles: EngineHandles,
    private val heldReferences: HeldReferences,
    private val heap: WasmHeap,
) : ModuleInstallation {

    override fun external(importable: Importable): ExternalValue {
        return handles.external(importable)
    }

    override fun config(storeConfig: RuntimeConfig): RuntimeConfig {
        return storeConfig
    }

    override fun trapSource(instance: ModuleInstance): TrapSource? {
        return trapSources.trapSource(decodedModule, instance)
    }

    override fun thrown(): ExceptionReference {
        return heldReferences.exception(heap.takePendingExceptionReference())
    }

    override suspend fun instantiate(
        config: RuntimeConfig,
        runtime: RuntimeStore,
        imports: List<RuntimeImport>,
    ): Result<ModuleInstance, ModuleTrapError> {
        return ParallelModuleInstantiator(
            config = config,
            store = runtime,
            module = decodedModule.declarations,
            imports = imports,
            taskExecutor = taskExecutor,
            codeInstaller = ParallelModuleCodeInstaller(decodedModule.module),
        )
    }
}
