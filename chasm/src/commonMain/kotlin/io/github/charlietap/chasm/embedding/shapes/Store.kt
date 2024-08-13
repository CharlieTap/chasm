package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.executor.runtime.store.Store as InternalStore

class Store internal constructor(internal val store: InternalStore = InternalStore())
