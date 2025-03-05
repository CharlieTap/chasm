package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

fun publicStore(
    store: InternalStore = store(),
) = Store(store)
