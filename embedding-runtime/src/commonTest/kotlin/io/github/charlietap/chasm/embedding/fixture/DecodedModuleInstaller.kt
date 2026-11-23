package io.github.charlietap.chasm.embedding.fixture

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.compiler.backend.chasm.ParallelModuleLinker
import io.github.charlietap.chasm.compiler.frontend.CompilationError
import io.github.charlietap.chasm.compiler.frontend.ParallelModuleCompiler
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.internal.ModuleInstaller
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleInstantiator
import io.github.charlietap.chasm.parallel.coroutines.DefaultCoroutineParallelTaskExecutor
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.embedding.error.InstantiationError as EmbeddingInstantiationError
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class DecodedModuleInstaller(
    private val trapSource: (ModuleInstance) -> TrapSource?,
) : ModuleInstaller {

    override fun rejection(module: Module): EmbeddingInstantiationError? {
        return if (module._payload() is DecodedModule) {
            null
        } else {
            ExecutionError.Unsupported("an interpreter store only instantiates modules decoded by module()")
        }
    }

    override fun config(
        module: Module,
        storeConfig: RuntimeConfig,
    ): RuntimeConfig {
        return storeConfig
    }

    override fun trapSource(
        module: Module,
        instance: ModuleInstance,
    ): TrapSource? {
        return trapSource(instance)
    }

    override suspend fun instantiate(
        module: Module,
        config: RuntimeConfig,
        runtime: RuntimeStore,
        imports: List<RuntimeImport>,
    ): Result<ModuleInstance, ModuleTrapError> {

        val decoded = module._payload() as DecodedModule

        return ParallelModuleInstantiator(
            config = config,
            store = runtime,
            module = decoded.declarations,
            imports = imports,
            taskExecutor = DefaultCoroutineParallelTaskExecutor,
            codeInstaller = { store, instance, _, types, taskExecutor ->
                ParallelModuleCompiler(decoded.module, types, taskExecutor)
                    .mapError { error -> moduleTrapError(error) }
                    .map { code -> ParallelModuleLinker(store, instance, decoded.module, code, taskExecutor) }
            },
        )
    }

    private fun moduleTrapError(error: CompilationError): ModuleTrapError {
        return when (error) {
            CompilationError.UnsupportedThreads -> InstantiationError.UnsupportedThreadsModule
            CompilationError.UnsupportedSimd -> InstantiationError.UnsupportedSIMDModule
        }
    }
}
