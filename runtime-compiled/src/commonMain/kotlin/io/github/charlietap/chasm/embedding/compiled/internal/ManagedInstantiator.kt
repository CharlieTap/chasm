package io.github.charlietap.chasm.embedding.compiled.internal

import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.internal.InstantiatedModule
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module

internal interface ManagedInstantiator {

    suspend fun instantiate(
        module: Module,
        imports: List<Import>,
    ): ChasmResult<InstantiatedModule, InstantiationError>
}
