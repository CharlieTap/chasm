package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.runtime.store.Store as InternalStore

fun store(): Store = Store(InternalStore())
