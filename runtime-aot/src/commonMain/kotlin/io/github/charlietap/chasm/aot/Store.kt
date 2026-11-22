@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.AotModuleInstaller
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(config: StoreConfig = StoreConfig()): Store {
    return interpreterEngine(StoreRuntime(RuntimeStore(config = config)), AotModuleInstaller()).store
}
