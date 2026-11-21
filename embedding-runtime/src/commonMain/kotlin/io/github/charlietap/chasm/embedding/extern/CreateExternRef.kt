package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ExternRef
import io.github.charlietap.chasm.embedding.shapes.ExternRef.Companion.nullExternRef
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun createExternRef(
    store: Store,
    value: Any?,
): ChasmResult<ExternRef, ChasmError.ExecutionError> {
    if (store.isDropped) {
        return ChasmResult.Error(ChasmError.ExecutionError("Store has been dropped"))
    }

    if (value == null) {
        return ChasmResult.Success(nullExternRef(store))
    }

    return try {
        ChasmResult.Success(
            ExternRef(
                owner = store,
                root = store.store.heap.createRetainedExtern(store.store, value),
            ),
        )
    } catch (failure: InvocationException) {
        ChasmResult.Error(ChasmError.ExecutionError(failure.error.toString()))
    }
}
