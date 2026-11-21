@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.jit.internal._store

fun store(): Store {
    return _store(StoreConfig())
}

fun store(config: StoreConfig): Store {
    return _store(config)
}
