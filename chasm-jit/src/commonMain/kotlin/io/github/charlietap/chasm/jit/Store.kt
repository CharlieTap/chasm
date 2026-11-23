@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.jit.internal._jitStore

fun store(config: RuntimeConfig = RuntimeConfig()): Store {
    return _jitStore(config)
}
