package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.TrapSource
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.Import as RuntimeImport
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
interface ModuleInstallation {

    fun external(importable: Importable): ExternalValue

    fun config(storeConfig: RuntimeConfig): RuntimeConfig

    fun trapSource(instance: ModuleInstance): TrapSource?

    fun thrown(): ExceptionReference

    suspend fun instantiate(
        config: RuntimeConfig,
        runtime: RuntimeStore,
        imports: List<RuntimeImport>,
    ): Result<ModuleInstance, ModuleTrapError>
}
