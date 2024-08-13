package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.executor.runtime.store.Store as InternalStore

fun publicStore(
    store: InternalStore = store(),
) = Store(store)
