package io.github.charlietap.chasm.compiled.script.host

import io.github.charlietap.chasm.compiled.Compiler
import io.github.charlietap.chasm.compiled.script.ScriptModules
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import kotlinx.coroutines.runBlocking

typealias HostModuleResolver = (Store, StoreConfig, Compiler) -> Instance

fun HostModuleResolver(
    store: Store,
    storeConfig: StoreConfig,
    compiler: Compiler,
): Instance {
    val bytes = checkNotNull(ScriptModules::class.java.getResourceAsStream(FILE_HOST_MODULE)) { "missing $FILE_HOST_MODULE" }.use { it.readBytes() }
    return compiler(bytes, ModuleConfig(), storeConfig)
        .flatMap { module -> runBlocking { instance(store, module) } }
        .expect("Failed to instantiate script host module")
}

private const val FILE_HOST_MODULE = "/script/spectest-host.wasm"
