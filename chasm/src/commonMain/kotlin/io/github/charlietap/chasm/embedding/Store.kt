package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

fun store(): Store = Store(InternalStore())
