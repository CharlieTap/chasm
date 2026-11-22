@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.jit.internal._jitStore

fun store(config: StoreConfig = StoreConfig()): Store {
    return _jitStore(config)
}
