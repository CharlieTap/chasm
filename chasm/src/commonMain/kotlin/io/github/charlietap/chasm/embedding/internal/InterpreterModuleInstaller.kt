@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.InterpreterTrapSource
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleInstantiator
import io.github.charlietap.chasm.parallel.coroutines.DefaultCoroutineParallelTaskExecutor
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class InterpreterModuleInstaller : ModuleInstaller {

    override fun rejection(module: Module): InstantiationError? {
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
        return InterpreterTrapSource(instance, module._payload() as DecodedModule)
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
            codeInstaller = ParallelModuleCodeInstaller(decoded.module),
        )
    }
}
