package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.ExternRef
import io.github.charlietap.chasm.embedding.shapes.ExternRef.Companion.nullExternRef
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun createExternRef(
    store: Store,
    value: Any?,
): ExternRef {

    check(!store.isDropped) { "Store has been dropped" }

    if (value == null) {
        return nullExternRef(store)
    }

    return try {
        ExternRef(
            owner = store,
            root = store.store.heap.createRetainedExtern(store.store, value),
        )
    } catch (failure: InvocationException) {
        throwAccessorFailure(failure.error)
    }
}
