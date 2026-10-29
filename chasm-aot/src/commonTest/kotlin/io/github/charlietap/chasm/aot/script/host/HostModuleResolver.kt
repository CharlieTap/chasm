package io.github.charlietap.chasm.aot.script.host

import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.aot.script.ScriptModules
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap

typealias HostModuleResolver = (Store, StoreConfig) -> Instance

fun HostModuleResolver(
    store: Store,
    storeConfig: StoreConfig,
): Instance {
    val bytes = checkNotNull(ScriptModules::class.java.getResourceAsStream(FILE_HOST_MODULE)) { "missing $FILE_HOST_MODULE" }.use { it.readBytes() }
    return ScriptModules.compile(bytes, ModuleConfig(), storeConfig)
        .flatMap { module -> instance(store, module, emptyList()) }
        .expect("Failed to instantiate script host module")
}

private const val FILE_HOST_MODULE = "/script/spectest-host.wasm"
