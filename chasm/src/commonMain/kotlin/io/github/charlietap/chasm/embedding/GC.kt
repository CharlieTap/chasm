package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

fun gc(
    store: Store,
): ChasmResult<Unit, ChasmError.ExecutionError> = gc(
    store = store,
    collector = ::GarbageCollector,
)

internal fun gc(
    store: Store,
    collector: GarbageCollector,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    return collector(store.store, null)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
