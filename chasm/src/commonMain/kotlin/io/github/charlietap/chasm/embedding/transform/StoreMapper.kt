package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

internal object StoreMapper : BidirectionalMapper<Store, InternalStore> {
    override fun map(input: Store): InternalStore = input.store

    override fun bimap(input: InternalStore): Store = Store(input)
}
