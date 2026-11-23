package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
interface ModuleInstaller {

    fun rejection(module: Module): InstantiationError?

    fun config(
        module: Module,
        storeConfig: RuntimeConfig,
    ): RuntimeConfig

    fun trapSource(
        module: Module,
        instance: ModuleInstance,
    ): TrapSource?

    suspend fun instantiate(
        module: Module,
        config: RuntimeConfig,
        runtime: RuntimeStore,
        imports: List<RuntimeImport>,
    ): Result<ModuleInstance, ModuleTrapError>
}
