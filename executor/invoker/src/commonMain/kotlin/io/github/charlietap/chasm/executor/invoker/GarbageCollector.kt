package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

/** Store-wide collector for managed guest aggregates and exceptions. */
typealias GarbageCollector = (Store, ValueStack?) -> Result<Unit, InvocationError>

fun GarbageCollector(
    store: Store,
    stack: ValueStack?,
): Result<Unit, InvocationError> {

    return try {
        store.heap.collectGarbage(store, stack)
        Ok(Unit)
    } catch (failure: OutOfMemoryError) {
        Err(
            InvocationError.GarbageCollectionFailed(
                failure.message ?: "collection exhausted host memory",
            ),
        )
    }
}
