@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.AotModule
import io.github.charlietap.chasm.aot.internal.AotModuleInstaller
import io.github.charlietap.chasm.embedding.internal.StoreRuntime
import io.github.charlietap.chasm.embedding.internal.interpreterEngine
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun store(module: Module): Store {

    val payload = requireNotNull(module._payload() as? AotModule) { "the module was not loaded by module(CompiledModule)" }

    return interpreterEngine(StoreRuntime(RuntimeStore(config = payload.config)), AotModuleInstaller(payload)).store
}
