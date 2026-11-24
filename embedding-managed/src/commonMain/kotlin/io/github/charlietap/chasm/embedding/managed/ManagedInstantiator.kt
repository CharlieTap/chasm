package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.internal.InstantiatedModule
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module

@InternalChasmApi
interface ManagedInstantiator {

    suspend fun instantiate(
        module: Module,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError>
}
