package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.resource.gc.GarbageCollector

fun gc(store: Store) {
    gc(
        store = store,
        collector = ::GarbageCollector,
    )
}

internal fun gc(
    store: Store,
    collector: GarbageCollector,
) {

    check(!store.isDropped) { InvocationError.StoreDropped.toString() }

    collector(store.store, null).getOrElse(::throwAccessorFailure)
}
