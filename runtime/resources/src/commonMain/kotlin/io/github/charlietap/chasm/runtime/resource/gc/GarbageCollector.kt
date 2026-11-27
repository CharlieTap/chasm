package io.github.charlietap.chasm.runtime.resource.gc

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.store.Store

/** Store-wide collector for managed guest aggregates and exceptions. */
@InternalChasmApi
typealias GarbageCollector = (Store) -> Result<Unit, InvocationError>

@InternalChasmApi
fun GarbageCollector(store: Store): Result<Unit, InvocationError> {
    return try {
        store.heap.collectGarbage(store)
        Ok(Unit)
    } catch (failure: GuestHeapOutOfMemoryException) {
        Err(
            InvocationError.GarbageCollectionFailed(
                failure.message ?: "collection exhausted host memory",
            ),
        )
    }
}
