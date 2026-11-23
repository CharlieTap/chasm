@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.internal.InterpreterModuleInstaller
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(config: RuntimeConfig = RuntimeConfig()): Store {
    return interpreterEngine(StoreRuntime(RuntimeStore(config = config)), InterpreterModuleInstaller()).store
}
